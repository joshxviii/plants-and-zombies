package joshxviii.plantz

import joshxviii.plantz.entity.PeaShooter
import joshxviii.plantz.entity.Plant
import joshxviii.plantz.entity.Sunflower
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

object PazEntities {

    @JvmField
    val SUNFLOWER: EntityType<Sunflower> = registerPlantEntity("sunflower", { _, l -> Sunflower(l) })
    @JvmField
    val PEA_SHOOTER: EntityType<PeaShooter> = registerPlantEntity("peashooter", { _, l -> PeaShooter(l) })

    fun <T: Plant> registerPlantEntity(
        name : String,
        factory: EntityType.EntityFactory<T>,
        category: MobCategory = MobCategory.CREATURE,
        width: Float = 0.75f,
        height: Float = 0.8f,
    ) : EntityType<T> {
        val key = ResourceKey.create(Registries.ENTITY_TYPE, pazResource(name))

        val type = EntityType.Builder.of(factory, category)
            .sized(width, height) // hitbox size
            .build(key)

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type)
    }

    fun registerAttributes(consumer: (EntityType<out LivingEntity>, AttributeSupplier.Builder) -> Unit) {
        consumer(SUNFLOWER, Plant.createAttributes())
        consumer(PEA_SHOOTER, Plant.createAttributes())
    }

    fun initialize() {}
}