package joshxviii.plantz

import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.*
import joshxviii.plantz.entity.projectile.*
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityDataRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob.createMobAttributes
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.projectile.Projectile

object PazEntities {
    private val typeToSunCost = mutableMapOf<EntityType<*>, Int>()
    fun getSunCostFromType(type: EntityType<*>) : Int {
        return typeToSunCost[type]?: 0
    }

    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlant(
        "sunflower",
        EntityType.Builder.of(::Sunflower, MobCategory.CREATURE),
        sunCost = 10,
        height = 1.3f,
    )
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlant(
        "peashooter",
        EntityType.Builder.of(::PeaShooter, MobCategory.CREATURE),
    )
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlant(
        "wallnut",
        EntityType.Builder.of(::WallNut, MobCategory.CREATURE),
        sunCost = 10,
        width = 1.0f,
        height = 1.1f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 40.0,
        )
    )
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlant(
        "chomper",
        EntityType.Builder.of(::Chomper, MobCategory.CREATURE),
        sunCost = 10,
        height = 1.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 35.0,
            attackDamage = 12.0,
            followRange = 3.75
        )
    )
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlant(
        "cherrybomb",
        EntityType.Builder.of(::CherryBomb, MobCategory.CREATURE),
        sunCost = 10,
        height = 0.5f,
    )
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlant(
        "potatomine",
        EntityType.Builder.of(::PotatoMine, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.8f,
        height = 0.35f,
    )
    @JvmField val ICE_PEA_SHOOTER: EntityType<IcePeaShooter> = registerPlant(
        "ice_peashooter",
        EntityType.Builder.of(::IcePeaShooter, MobCategory.CREATURE),
        sunCost = 10,
    )
    @JvmField val REPEATER: EntityType<Repeater> = registerPlant(
        "repeater",
        EntityType.Builder.of(::Repeater, MobCategory.CREATURE),
        sunCost = 10,
    )
    @JvmField val FIRE_PEA_SHOOTER: EntityType<FirePeaShooter> = registerPlant(
        "fire_peashooter",
        EntityType.Builder.of(::FirePeaShooter, MobCategory.CREATURE).fireImmune(),
        sunCost = 10,
    )
    @JvmField val CACTUS: EntityType<Cactus> = registerPlant(
        "cactus",
        EntityType.Builder.of(::Cactus, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.8f,
        height = 1.25f,
        eyeHeight = 0.85f,
    )
    @JvmField val MELON_PULT: EntityType<MelonPult> = registerPlant(
        "melonpult",
        EntityType.Builder.of(::MelonPult, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.9f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 50.0,
            attackDamage = 20.0,
            followRange = 20.0
        )
    )
    @JvmField val PUFF_SHROOM: EntityType<PuffShroom> = registerPlant(
        "puffshroom",
        EntityType.Builder.of(::PuffShroom, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.5f,
        height = 0.65f,
        eyeHeight = 0.3f
    )
    @JvmField val FUME_SHROOM: EntityType<FumeShroom> = registerPlant(
        "fumeshroom",
        EntityType.Builder.of(::FumeShroom, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.8f,
        height = 0.8f
    )
    @JvmField val SUN_SHROOM: EntityType<SunShroom> = registerPlant(
        "sunshroom",
        EntityType.Builder.of(::SunShroom, MobCategory.CREATURE),
        sunCost = 10,
        height = 0.85f
    )

    //Projectiles
    @JvmField val PEA: EntityType<Pea> = registerProjectile("pea", EntityType.Builder.of(::Pea, MobCategory.MISC))
    @JvmField val PEA_ICE: EntityType<PeaIce> = registerProjectile("pea_ice", EntityType.Builder.of(::PeaIce, MobCategory.MISC))
    @JvmField val PEA_FIRE: EntityType<PeaFire> = registerProjectile("pea_fire", EntityType.Builder.of(::PeaFire, MobCategory.MISC))
    @JvmField val NEEDLE: EntityType<Needle> = registerProjectile("needle", EntityType.Builder.of(::Needle, MobCategory.MISC))
    @JvmField val SPORE: EntityType<Spore> = registerProjectile("spore", EntityType.Builder.of(::Spore, MobCategory.MISC))
    @JvmField val MELON: EntityType<Melon> = registerProjectile("melon", EntityType.Builder.of(::Melon, MobCategory.MISC), width = 1.0f, height = 0.8f)

    //Other
    @JvmField val PLANT_POT_MINECART: EntityType<PlantPotMinecart> = register(
        "plant_pot_minecart",
        EntityType.Builder.of(::PlantPotMinecart, MobCategory.MISC)
            .noLootTable()
            .sized(0.98F, 0.7F)
            .passengerAttachments(0.75F)
            .clientTrackingRange(8)
    )

    private fun <T : LivingEntity> registerPlant(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.CREATURE),
        sunCost: Int = 0,
        width: Float = 0.6f,
        height: Float = 1.0f,
        eyeHeight: Float = height * 0.85f,
        attributes: Plant.Companion.PlantAttributes = Plant.Companion.PlantAttributes()
    ): EntityType<T> {
        builder.sized(width, height).eyeHeight(eyeHeight)
        val type = register(name, builder)
        FabricDefaultAttributeRegistry.register(type, attributes.copy(sunCost = sunCost).apply(createMobAttributes()))
        typeToSunCost[type] = sunCost
        return type
    }
    private fun <T : Projectile> registerProjectile(
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

    @JvmField val TAG_PLANTS = registerEntityTag("plants")
    @JvmField val TAG_CANNOT_CHOMP = registerEntityTag("cannot_be_chomped")
    @JvmField val ZOMBIE_RAIDERS = registerEntityTag("zombie_raiders")
    private fun registerEntityTag(name: String) = TagKey.create(Registries.ENTITY_TYPE, pazResource(name))

    fun initialize() {}
}