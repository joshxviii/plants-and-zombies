package joshxviii.plantz

import joshxviii.plantz.entity.*
import joshxviii.plantz.entity.projectile.Pea
import joshxviii.plantz.entity.projectile.PeaFire
import joshxviii.plantz.entity.projectile.PeaIce
import joshxviii.plantz.entity.projectile.PlantProjectile
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

object PazEntities {

    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlant(
        "sunflower",
        EntityType.Builder.of(::Sunflower, MobCategory.CREATURE),
        height = 1.3f,
    )
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlant(
        "peashooter",
        EntityType.Builder.of(::PeaShooter, MobCategory.CREATURE),
    )
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlant(
        "wallnut",
        EntityType.Builder.of(::WallNut, MobCategory.CREATURE),
        width = 1.0f,
        height = 1.1f
    )
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlant(
        "chomper",
        EntityType.Builder.of(::Chomper, MobCategory.CREATURE),
        height = 1.5f
    )
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlant(
        "cherrybomb",
        EntityType.Builder.of(::CherryBomb, MobCategory.CREATURE),
        height = 0.5f
    )
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlant(
        "potatomine",
        EntityType.Builder.of(::PotatoMine, MobCategory.CREATURE),
        width = 0.8f,
        height = 0.35f
    )
    @JvmField val ICE_PEA_SHOOTER: EntityType<IcePeaShooter> = registerPlant(
        "ice_peashooter",
        EntityType.Builder.of(::IcePeaShooter, MobCategory.CREATURE),
    )
    @JvmField val REPEATER: EntityType<Repeater> = registerPlant(
        "repeater",
        EntityType.Builder.of(::Repeater, MobCategory.CREATURE),
    )
    @JvmField val FIRE_PEA_SHOOTER: EntityType<FirePeaShooter> = registerPlant(
        "fire_peashooter",
        EntityType.Builder.of(::FirePeaShooter, MobCategory.CREATURE).fireImmune(),
    )

    //Projectiles
    @JvmField val PEA: EntityType<Pea> = registerProjectile("pea", EntityType.Builder.of(::Pea, MobCategory.MISC))
    @JvmField val PEA_ICE: EntityType<PeaIce> = registerProjectile("pea_ice", EntityType.Builder.of(::PeaIce, MobCategory.MISC))
    @JvmField val PEA_FIRE: EntityType<PeaFire> = registerProjectile("pea_fire", EntityType.Builder.of(::PeaFire, MobCategory.MISC))

    fun registerAttributes(consumer: (EntityType<out LivingEntity>, AttributeSupplier.Builder) -> Unit) {
        consumer(SUNFLOWER, Plant.createAttributes())
        consumer(PEA_SHOOTER, Plant.createAttributes())
        consumer(WALL_NUT, Plant.createAttributes())
        consumer(CHOMPER, Plant.createAttributes())
        consumer(CHERRY_BOMB, Plant.createAttributes())
        consumer(POTATO_MINE, Plant.createAttributes())
        consumer(ICE_PEA_SHOOTER, Plant.createAttributes())
        consumer(REPEATER, Plant.createAttributes())
        consumer(FIRE_PEA_SHOOTER, Plant.createAttributes())

    }

    private fun <T : Plant> registerPlant(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.CREATURE),
        width: Float = 0.6f,
        height: Float = 1.0f,
    ): EntityType<T> {
        builder.sized(width, height)
        return register(name, builder)
    }
    private fun <T : PlantProjectile> registerProjectile(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.MISC),
        width: Float = 0.3125f,
        height: Float = 0.3125f
    ): EntityType<T> {
        builder.sized(width, height).eyeHeight(0.0f)
        return register(name, builder)
    }

    private fun <T : Entity> register(
        name : String,
        builder: EntityType.Builder<T>
    ): EntityType<T> {
        val id = ResourceKey.create(Registries.ENTITY_TYPE, pazResource(name))
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id))
    }

    @JvmField val PLANTS = registerEntityTag("plants")
    private fun registerEntityTag(name: String) = TagKey.create(Registries.ENTITY_TYPE, pazResource(name))

    fun initialize() {}
}