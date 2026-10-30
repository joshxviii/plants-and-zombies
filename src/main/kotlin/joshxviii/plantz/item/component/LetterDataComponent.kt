package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer
import java.util.function.IntFunction

data class LetterDataComponent(
    val background: LetterBackground = LetterBackground.DEFAULT,
    val font: LetterFont = LetterFont.BASIC,
    val item: ItemStack = ItemStack.EMPTY,
    val pages: List<String> = emptyList(),
    val sign: Boolean = false,
    val author: String = "",
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        if (!item.isEmpty) consumer.accept(
            Component.translatable("tooltip.plantz.letter_item", item.hoverName, item.count).withStyle(ChatFormatting.GRAY)
        )
    }

    companion object {
        val CODEC: Codec<LetterDataComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                LetterBackground.CODEC.optionalFieldOf("background", LetterBackground.DEFAULT)
                    .forGetter(LetterDataComponent::background),
                LetterFont.CODEC.optionalFieldOf("font", LetterFont.BASIC).forGetter(LetterDataComponent::font),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(LetterDataComponent::item),
                Codec.string(0, 1024).sizeLimitedListOf(100).optionalFieldOf("pages", emptyList())
                    .forGetter(LetterDataComponent::pages),
                Codec.BOOL.optionalFieldOf("sealed", false).forGetter(LetterDataComponent::sign),
                Codec.STRING.optionalFieldOf("author", "").forGetter(LetterDataComponent::author)
            ).apply(instance) { background, font, item, pages, sealed, author ->
                LetterDataComponent(background, font, item, pages, sealed, author)
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, LetterDataComponent> = StreamCodec.composite(
            LetterBackground.STREAM_CODEC,
            LetterDataComponent::background,
            LetterFont.STREAM_CODEC,
            LetterDataComponent::font,
            ItemStack.OPTIONAL_STREAM_CODEC,
            LetterDataComponent::item,
            ByteBufCodecs.stringUtf8(1024).apply(ByteBufCodecs.list(100)),
            LetterDataComponent::pages,
            ByteBufCodecs.BOOL,
            LetterDataComponent::sign,
            ByteBufCodecs.stringUtf8(64),
            LetterDataComponent::author,
            ::LetterDataComponent
        )
    }
}

enum class LetterBackground(val id: Int) : StringRepresentable {
    DEFAULT(0),
    HEARTS(1),
    FANCY(2),
    TORN(3);

    val translationKey: String
        get() = "letter_background.plantz.${name.lowercase()}"

    override fun getSerializedName(): String = this.name.lowercase()
    fun getBackground(): Identifier {
        return pazResource("textures/gui/letter/background_${name.lowercase()}.png")
    }

    companion object {
        private val BY_ID: IntFunction<LetterBackground> = ByIdMap.continuous(
            LetterBackground::id,
            LetterBackground.entries.toTypedArray(),
            ByIdMap.OutOfBoundsStrategy.ZERO
        )
        val CODEC: Codec<LetterBackground> = StringRepresentable.fromEnum(LetterBackground::values)
        val STREAM_CODEC: StreamCodec<ByteBuf, LetterBackground> = ByteBufCodecs.idMapper(BY_ID, LetterBackground::id)
    }
}

enum class LetterFont(val id: Int) : StringRepresentable {
    BASIC(0),
    HAND_WRITTEN(1),
    FANCY(2);

    override fun getSerializedName(): String = this.name.lowercase()

    val translationKey: String
        get() = "letter_font.plantz.${name.lowercase()}"

    fun getFont(): Identifier = Identifier.withDefaultNamespace(
        when (this) {
            BASIC -> "default"
            HAND_WRITTEN -> "alt"
            FANCY -> "uniform"
        }
    )

    companion object {
        private val BY_ID: IntFunction<LetterFont> =
            ByIdMap.continuous(LetterFont::id, LetterFont.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)
        val CODEC: Codec<LetterFont> = StringRepresentable.fromEnum(LetterFont::values)
        val STREAM_CODEC: StreamCodec<ByteBuf, LetterFont> = ByteBufCodecs.idMapper(BY_ID, LetterFont::id)
    }
}