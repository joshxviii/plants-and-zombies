package joshxviii.plantz

import joshxviii.plantz.entity.CherryBomb
import joshxviii.plantz.entity.Chomper
import joshxviii.plantz.entity.IcePea
import joshxviii.plantz.entity.PeaShooter
import joshxviii.plantz.entity.Plant
import joshxviii.plantz.entity.PotatoMine
import joshxviii.plantz.entity.Repeater
import joshxviii.plantz.entity.Sunflower
import joshxviii.plantz.entity.WallNut
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.level.Level

object PazEntities {

    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlantEntity("sunflower", ::Sunflower,
        width = 0.6f,
        height = 1.3f,
    )
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlantEntity("peashooter", ::PeaShooter)
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlantEntity("wallnut", ::WallNut,
        width = 0.6f,
        height = 0.25f
    )
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlantEntity("chomper", ::Chomper,
        width = 0.6f,
        height = 0.25f
    )
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlantEntity("cherrybomb", ::CherryBomb,
        width = 0.6f,
        height = 0.25f
    )
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlantEntity("potatomine", ::PotatoMine,
        width = 0.8f,
        height = 0.35f
    )
    @JvmField val ICE_PEA: EntityType<IcePea> = registerPlantEntity("icepea", ::IcePea)
    @JvmField val REPEATER: EntityType<Repeater> = registerPlantEntity("repeater", ::Repeater)

    fun registerAttributes(consumer: (EntityType<out LivingEntity>, AttributeSupplier.Builder) -> Unit) {
        consumer(SUNFLOWER, Plant.createAttributes())
        consumer(PEA_SHOOTER, Plant.createAttributes())
        consumer(WALL_NUT, Plant.createAttributes())
        consumer(CHOMPER, Plant.createAttributes())
        consumer(CHERRY_BOMB, Plant.createAttributes())
        consumer(POTATO_MINE, Plant.createAttributes())
        consumer(ICE_PEA, Plant.createAttributes())
        consumer(REPEATER, Plant.createAttributes())
    }

    fun <T: Plant> registerPlantEntity(
        name : String,
        factory: (Level) -> T,
        category: MobCategory = MobCategory.CREATURE,
        width: Float = 0.6f,
        height: Float = 1.0f,
    ) : EntityType<T> {
        val key = ResourceKey.create(Registries.ENTITY_TYPE, pazResource(name))

        val type = EntityType.Builder.of({ type, level ->
            factory(level).apply { type }
        }, category)
            .sized(width, height)
            .build(key)

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type)
    }

    fun initialize() {}
}