package joshxviii.plantz

import PazDataSerializers
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.impl.item.ItemComponentTooltipProviderRegistryImpl
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.equipment.Equippable
import org.apache.logging.log4j.LogManager

object PazMain : ModInitializer {
	const val MODID = "plantz"
    private val logger = LogManager.getLogger(MODID)

	override fun onInitialize() {
		PazServerParticles.initialize()
		PazBlocks.initialize()
		PazItems.initialize()
		PazCreativeTab.initialize()
		PazEntities.initialize()
		PazDamageTypes.initialize()
		PazDataSerializers.initialize()
		PazAttributes.initialize()

		ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SEED_PACKET)
		ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SUN_COST)

		DefaultItemComponentEvents.MODIFY.register {
			it.modify(Items.BUCKET) { builder ->
				builder.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
					.setEquipSound(SoundEvents.ARMOR_EQUIP_IRON)
					.build()
				)

				val armorModifier = ItemAttributeModifiers.builder()
					.add(
						Attributes.ARMOR,
						AttributeModifier(pazResource("bucket_armor"), 1.0, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.HEAD
					).add(
						Attributes.KNOCKBACK_RESISTANCE,
						AttributeModifier(pazResource("bucket_knockback_resistance"), 0.1, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.HEAD
					).build()

				builder.set(DataComponents.ATTRIBUTE_MODIFIERS, armorModifier)
			}
		}
	}
}