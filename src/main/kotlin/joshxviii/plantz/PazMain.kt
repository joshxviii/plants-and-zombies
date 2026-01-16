package joshxviii.plantz

import PazDataSerializers
import joshxviii.plantz.PazTags.EntityTypes.ATTACKS_PLANTS
import joshxviii.plantz.entity.plants.Plant
import joshxviii.plantz.entity.plants.WallNut
import joshxviii.plantz.mixin.MobAccessor
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.impl.item.ItemComponentTooltipProviderRegistryImpl
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
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
		PazEffects.initialize()



		ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SEED_PACKET)
		ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SUN_COST)

		ServerEntityEvents.ENTITY_LOAD.register { entity, level ->
			if (entity is Mob && entity.`is`(ATTACKS_PLANTS)) {
				(entity as MobAccessor).targetSelector.addGoal(1, NearestAttackableTargetGoal(entity, WallNut::class.java, 4, true, true) { target, level -> target is WallNut })
				(entity as MobAccessor).targetSelector.addGoal(4, NearestAttackableTargetGoal(entity, Plant::class.java, 5, true, false) { target, level -> target is Plant })
			}
		}

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