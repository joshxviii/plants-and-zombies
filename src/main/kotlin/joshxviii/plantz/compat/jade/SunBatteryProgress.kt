package joshxviii.plantz.compat.jade

import joshxviii.plantz.PazComponents
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.item.component.StoredSun
import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import snownee.jade.api.Accessor
import snownee.jade.api.ui.MessageType
import snownee.jade.api.view.ClientViewGroup
import snownee.jade.api.view.IClientExtensionProvider
import snownee.jade.api.view.IServerExtensionProvider
import snownee.jade.api.view.ProgressView
import snownee.jade.api.view.ViewGroup

object SunBatteryProgress : IServerExtensionProvider<ProgressView.Data>, IClientExtensionProvider<ProgressView.Data, ProgressView> {
    private val UID: Identifier = pazResource("sun_battery")

    override fun getUid(): Identifier = UID

    override fun getGroups(accessor: Accessor<*>): List<ViewGroup<ProgressView.Data>> {
        val sun = (accessor.target as? SunBatteryBlockEntity)?.theItem?.get(PazComponents.STORED_SUN) ?: StoredSun()
        val group = ViewGroup(listOf(ProgressView.Data(sun.storagePercentage(), MessageType.WARNING)))
        group.extraData.putInt("Sun", sun.storedSun)
        group.extraData.putInt("Max", sun.max)
        return listOf(group)
    }

    override fun getClientGroups(accessor: Accessor<*>, groups: List<ViewGroup<ProgressView.Data>>): List<ClientViewGroup<ProgressView>> =
        ClientViewGroup.map(groups, ProgressView::read) { group, clientGroup ->
            val sun = group.extraData.getIntOr("Sun", 0)
            val max = group.extraData.getIntOr("Max", 0)
            clientGroup.views.forEach {
                it.text = Component.translatable("component.plantz.stored_sun", sun, max).withStyle(ChatFormatting.YELLOW)
            }
        }
}
