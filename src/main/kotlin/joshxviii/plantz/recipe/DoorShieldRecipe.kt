package joshxviii.plantz.recipe

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.item.component.DoorShieldComponent
import joshxviii.plantz.pazResource
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.BlockItemTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.display.RecipeDisplay
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay
import net.minecraft.world.item.crafting.display.SlotDisplay
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import java.util.Optional

class DoorShieldRecipe : CustomRecipe() {
    override fun matches(input: CraftingInput, level: Level): Boolean {
        return input.ingredientCount() == 2 && doorBlock(input) != null &&
            input.items().count { it.`is`(Items.SHIELD) } == 1
    }

    override fun assemble(input: CraftingInput): ItemStack {
        val door = doorBlock(input) ?: return ItemStack.EMPTY
        return ItemStack(PazItems.DOOR_SHIELD).also {
            it.set(PazComponents.DOOR_SHIELD, DoorShieldComponent(door))
        }
    }

    override fun category(): CraftingBookCategory = CraftingBookCategory.EQUIPMENT

    override fun display(): List<RecipeDisplay> = listOf(

    )

    override fun getSerializer(): RecipeSerializer<DoorShieldRecipe> = SERIALIZER

    private fun doorBlock(input: CraftingInput): Block? {
        if (input.ingredientCount() != 2) return null

        val doorItem = input.items().singleOrNull { it.`is`(BlockItemTags.DOORS.item()) } ?: return null
        val doorBlock = (doorItem.item as? BlockItem)?.block ?: return null
        return doorBlock.takeIf { it is DoorBlock }
    }

    companion object {
        val CODEC: MapCodec<DoorShieldRecipe> = MapCodec.unit(::DoorShieldRecipe)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DoorShieldRecipe> =
            StreamCodec.unit(DoorShieldRecipe())
        val SERIALIZER = RecipeSerializer(CODEC, STREAM_CODEC)

        fun register() {
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, pazResource("door_shield"), SERIALIZER)
        }
    }
}
