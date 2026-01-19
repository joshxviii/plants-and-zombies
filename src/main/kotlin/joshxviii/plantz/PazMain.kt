package joshxviii.plantz

import PazDataSerializers
import joshxviii.plantz.PazTags.EntityTypes.ATTACKS_PLANTS
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plants.WallNut
import joshxviii.plantz.item.component.BlocksHeadDamage
import joshxviii.plantz.mixin.MobAccessor
import joshxviii.plantz.mixin.SpawnPlacementsInvoker
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.impl.item.ItemComponentTooltipProviderRegistryImpl
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.Heightmap
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
		PazSounds.initialize()
		PazSpawnPlacements.initialize()
	}
}

