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

object PazEntities {

    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlantEntity("sunflower", { _, l -> Sunflower(l) })
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlantEntity("peashooter", { _, l -> PeaShooter(l) })
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlantEntity("wallnut", { _, l -> WallNut(l) })
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlantEntity("chomper", { _, l -> Chomper(l) })
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlantEntity("cherrybomb", { _, l -> CherryBomb(l) })
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlantEntity("potatomine", { _, l -> PotatoMine(l) })
    @JvmField val ICE_PEA: EntityType<IcePea> = registerPlantEntity("icepea", { _, l -> IcePea(l) })
    @JvmField val REPEATER: EntityType<Repeater> = registerPlantEntity("repeater", { _, l -> Repeater(l) })

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

    fun initialize() {}
}