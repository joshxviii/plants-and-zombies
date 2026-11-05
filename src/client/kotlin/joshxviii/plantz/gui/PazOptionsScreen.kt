package joshxviii.plantz.gui

import joshxviii.plantz.PazConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

class ToggleOption(
    val labelKey: String,
    val get: () -> Boolean,
    val set: (Boolean) -> Unit,
) {
    fun label(): Component = Component.translatable(
        labelKey,
        if (get()) CommonComponents.OPTION_ON else CommonComponents.OPTION_OFF
    )
}

object PazClientOptions {
    val ALL: List<ToggleOption> = listOf(
        ToggleOption(
            "options.plantz.show_raid_ui",
            get = { PazConfig.client.showRaidUi },
            set = { PazConfig.client.showRaidUi = it },
        ),
        ToggleOption(
            "options.plantz.show_sun_info",
            get = { PazConfig.client.showSunInfo },
            set = { PazConfig.client.showSunInfo = it },
        ),
        ToggleOption(
            "options.plantz.split_item_tabs",
            get = { PazConfig.client.splitItemTabs },
            set = { PazConfig.client.splitItemTabs = it },
        ),
    )
}

class PazOptionsScreen(private val parent: Screen? = null) : Screen(Component.translatable("options.plantz.title")) {
    private companion object {
        const val HEADER_HEIGHT = 32
        const val FOOTER_HEIGHT = 36
        const val DONE_WIDTH = 100
        const val BUTTON_HEIGHT = 20
        const val MARGIN = 10
    }

    private inner class OptionList(listWidth: Int, listHeight: Int) :
        ContainerObjectSelectionList<OptionList.Row>(minecraft!!, listWidth, listHeight, HEADER_HEIGHT, BUTTON_HEIGHT + 6) {

        init {
            for (option in PazClientOptions.ALL) addEntry(Row(option))
        }

        override fun getRowWidth(): Int = this.width - 24

        inner class Row(option: ToggleOption) : Entry<Row>() {
            private val button: Button = Button.builder(option.label()) { b ->
                option.set(!option.get())
                b.message = option.label()
                PazConfig.saveClient()
            }.bounds(0, 0, 160, BUTTON_HEIGHT).build()

            override fun children(): List<GuiEventListener> = listOf(button)
            override fun narratables(): List<NarratableEntry> = listOf(button)

            override fun extractContent(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, hovered: Boolean, partialTick: Float) {
                button.x = contentX
                button.y = contentY
                button.width = contentWidth
                button.extractRenderState(graphics, mouseX, mouseY, partialTick)
            }
        }
    }

    private var list: OptionList? = null

    override fun init() {
        val listWidth = (width * 0.5).toInt().coerceIn(180, 320).coerceAtMost(width)
        val newList = OptionList(listWidth, height - HEADER_HEIGHT)
        newList.updateSizeAndPosition(listWidth, height - HEADER_HEIGHT, HEADER_HEIGHT)
        newList.x = 0
        list = addRenderableWidget(newList)

        addRenderableWidget(
            Button.builder(CommonComponents.GUI_DONE) { onClose() }
                .bounds(width - DONE_WIDTH - MARGIN, height - FOOTER_HEIGHT + (FOOTER_HEIGHT - BUTTON_HEIGHT) / 2, DONE_WIDTH, BUTTON_HEIGHT)
                .build()
        )
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)
        graphics.text(font, title, MARGIN, (HEADER_HEIGHT - font.lineHeight) / 2, -1, true)
    }

    override fun onClose() {
        Minecraft.getInstance().setScreen(parent)
    }
}
