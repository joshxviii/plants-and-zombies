package joshxviii.plantz

import joshxviii.plantz.PazTags.EntityTypes.ATTACKS_PLANTS
import joshxviii.plantz.PazTags.EntityTypes.IGNORED_BY_PLANT_ATTACKERS
import joshxviii.plantz.entity.PlantPotMinecart
import joshxviii.plantz.entity.Sun
import joshxviii.plantz.entity.gnome.Gnome
import joshxviii.plantz.entity.plant.*
import joshxviii.plantz.entity.plants.WallNut
import joshxviii.plantz.entity.projectile.*
import joshxviii.plantz.entity.zombie.BackupDancer
import joshxviii.plantz.entity.zombie.BrownCoat
import joshxviii.plantz.entity.zombie.DiscoZombie
import joshxviii.plantz.entity.zombie.Miner
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.mixin.MobAccessor
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.*
import net.minecraft.world.entity.Mob.createMobAttributes
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard

object PazEntities {
    private fun Scoreboard.createPlantTeam(): PlayerTeam {
        val plantTeam = this.addPlayerTeam("plantz")
        plantTeam.setSeeFriendlyInvisibles(false)
        plantTeam.isAllowFriendlyFire = false
        return plantTeam
    }

    fun initialize() {
        ServerLevelEvents.LOAD.register { server, level ->
            val scoreboard: Scoreboard = server.scoreboard
            PLANT_TEAM = scoreboard.getPlayerTeam("plantz")?: scoreboard.createPlantTeam()
        }

        ServerEntityEvents.ENTITY_LOAD.register { entity, level ->

            if (entity is Zombie) (entity as MobAccessor).targetSelector.addGoal(4, NearestAttackableTargetGoal(entity, Gnome::class.java, 5, true, false, null))

            if (entity is Mob && entity.`is`(ATTACKS_PLANTS)) {
                (entity as MobAccessor).targetSelector.addGoal(1, NearestAttackableTargetGoal(entity, WallNut::class.java, 4, true, true) { target, level -> target is WallNut })
                (entity as MobAccessor).targetSelector.addGoal(4, NearestAttackableTargetGoal(entity, Plant::class.java, 5, true, false) { target, level ->
                    target is Plant && !target.`is`(IGNORED_BY_PLANT_ATTACKERS) })
            }
        }
    }

    private val typeToSunCost = mutableMapOf<EntityType<*>, Int>()
    fun getSunCostFromType(type: EntityType<*>) : Int = typeToSunCost[type]?: 0

    // region Plants
    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlant(
        "sunflower",
        EntityType.Builder.of(::Sunflower, MobCategory.CREATURE),
        sunCost = 5,
        height = 1.1f,
    )
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlant(
        "peashooter",
        EntityType.Builder.of(::PeaShooter, MobCategory.CREATURE),
        sunCost = 5,
    )
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlant(
        "wallnut",
        EntityType.Builder.of(::WallNut, MobCategory.CREATURE),
        sunCost = 5,
        width = 1.0f,
        height = 1.1f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 40.0,
        )
    )
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlant(
        "chomper",
        EntityType.Builder.of(::Chomper, MobCategory.CREATURE),
        sunCost = 7,
        height = 1.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 35.0,
            attackDamage = 12.0,
            attackKnockback = 0.1,
            followRange = 3.75
        )
    )
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlant(
        "cherrybomb",
        EntityType.Builder.of(::CherryBomb, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.8f,
        height = 0.76f,
    )
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlant(
        "potatomine",
        EntityType.Builder.of(::PotatoMine, MobCategory.CREATURE),
        sunCost = 5,
        width = 0.8f,
        height = 0.35f,
    )
    @JvmField val ICE_PEA_SHOOTER: EntityType<IcePeaShooter> = registerPlant(
        "ice_peashooter",
        EntityType.Builder.of(::IcePeaShooter, MobCategory.CREATURE),
        sunCost = 7,
    )
    @JvmField val REPEATER: EntityType<Repeater> = registerPlant(
        "repeater",
        EntityType.Builder.of(::Repeater, MobCategory.CREATURE),
        sunCost = 7,
    )
    @JvmField val FIRE_PEA_SHOOTER: EntityType<FirePeaShooter> = registerPlant(
        "fire_peashooter",
        EntityType.Builder.of(::FirePeaShooter, MobCategory.CREATURE).fireImmune(),
        sunCost = 7,
    )
    @JvmField val CACTUS: EntityType<Cactus> = registerPlant(
        "cactus",
        EntityType.Builder.of(::Cactus, MobCategory.CREATURE),
        sunCost = 7,
        width = 0.8f,
        height = 1.25f,
        eyeHeight = 0.85f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 2.0,
            followRange = 34.0
        )
    )
    @JvmField val MELON_PULT: EntityType<MelonPult> = registerPlant(
        "melonpult",
        EntityType.Builder.of(::MelonPult, MobCategory.CREATURE),
        sunCost = 10,
        width = 0.9f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 50.0,
            attackDamage = 3.0,
            attackKnockback = 0.5,
            followRange = 24.0
        )
    )
    @JvmField val PUFF_SHROOM: EntityType<PuffShroom> = registerPlant(
        "puffshroom", EntityType.Builder.of(::PuffShroom, MobCategory.CREATURE),
        sunCost = 0,
        width = 0.5f,
        height = 0.65f,
        eyeHeight = 0.3f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 10.0,
            attackDamage = 2.0,
            followRange = 10.0
        )
    )
    @JvmField val SCAREDY_SHROOM: EntityType<ScaredyShroom> = registerPlant(
        "scaredyshroom", EntityType.Builder.of(::ScaredyShroom, MobCategory.CREATURE),
        sunCost = 3,
        width = 0.45f,
        height = 0.9f,
        eyeHeight = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            followRange = 5.0
        )
    )
    @JvmField val FUME_SHROOM: EntityType<FumeShroom> = registerPlant(
        "fumeshroom",
        EntityType.Builder.of(::FumeShroom, MobCategory.CREATURE),
        sunCost = 7,
        width = 0.8f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.0,
        )
    )
    @JvmField val SUN_SHROOM: EntityType<SunShroom> = registerPlant(
        "sunshroom",
        EntityType.Builder.of(::SunShroom, MobCategory.CREATURE),
        sunCost = 3,
        height = 0.85f
    )
    @JvmField val HYPNOSHROOM: EntityType<HypnoShroom> = registerPlant(
        "hypnoshroom", EntityType.Builder.of(::HypnoShroom, MobCategory.CREATURE),
        sunCost = 7,
        width = 0.6f,
        height = 1.3f,
        eyeHeight = 0.3f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 2.0,
            followRange = 20.0
        )
    )
    // endregion

    // region Zombies
    @JvmField val BROWN_COAT: EntityType<BrownCoat> =  registerZombie(
        "browncoat",
        EntityType.Builder.of(::BrownCoat, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8)
            .notInPeaceful(),
        attributes = Zombie.createAttributes()
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 10.0)
    )
    @JvmField val MINER: EntityType<Miner> =  registerZombie(
        "miner",
        EntityType.Builder.of(::Miner, MobCategory.MONSTER)
            .sized(0.63f, 1.95f)
            .eyeHeight(1.74f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8)
            .notInPeaceful(),
        attributes = Zombie.createAttributes()
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MAX_HEALTH, 25.0)
            .add(Attributes.MOVEMENT_SPEED, 0.21)
            .add(Attributes.FOLLOW_RANGE, 9.0)
    )
    @JvmField val ZOMBIE_YETI: EntityType<ZombieYeti> =  registerZombie(
        "zombie_yeti",
        EntityType.Builder.of(::ZombieYeti, MobCategory.MONSTER)
            .sized(1.25f, 2.6f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8)
            .notInPeaceful(),
        attributes = Zombie.createAttributes()
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.MAX_HEALTH, 80.0)
            .add(Attributes.MOVEMENT_SPEED, 0.27)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
            .add(Attributes.SCALE, 1.25)
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(Attributes.ENTITY_INTERACTION_RANGE, 2.5)
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, -10.0)
    )
    @JvmField val DISCO_ZOMBIE: EntityType<DiscoZombie> =  registerZombie(
        "disco_zombie",
        EntityType.Builder.of(::DiscoZombie, MobCategory.MONSTER)
            .sized(0.64f, 2.0f)
            .eyeHeight(1.74f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8)
            .notInPeaceful(),
        attributes = Zombie.createAttributes()
            .add(Attributes.MAX_HEALTH, 35.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
    )
    @JvmField val BACKUP_DANCER: EntityType<BackupDancer> =  registerZombie(
        "backup_dancer",
        EntityType.Builder.of(::BackupDancer, MobCategory.MONSTER)
            .sized(0.64f, 2.0f)
            .eyeHeight(1.74f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8)
            .notInPeaceful(),
        attributes = Zombie.createAttributes()
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MAX_HEALTH, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.FOLLOW_RANGE, 16.0)
    )
    // endregion

    @JvmField val GNOME: EntityType<Gnome> =  registerGnome(
        "gnome",
        EntityType.Builder.of(::Gnome, MobCategory.MONSTER)
            .sized(0.4f, 0.78f)
            .ridingOffset(-0.15f),
        attributes = createMobAttributes()
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(Attributes.MAX_HEALTH, 25.0)
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.JUMP_STRENGTH, 0.4)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
    )

    //region Projectiles
    @JvmField val PEA: EntityType<Pea> = registerProjectile("pea", EntityType.Builder.of(::Pea, MobCategory.MISC))
    @JvmField val PEA_ICE: EntityType<PeaIce> = registerProjectile("pea_ice", EntityType.Builder.of(::PeaIce, MobCategory.MISC))
    @JvmField val PEA_FIRE: EntityType<PeaFire> = registerProjectile("pea_fire", EntityType.Builder.of(::PeaFire, MobCategory.MISC))
    @JvmField val NEEDLE: EntityType<Needle> = registerProjectile("needle", EntityType.Builder.of(::Needle, MobCategory.MISC))
    @JvmField val SPORE: EntityType<Spore> = registerProjectile("spore", EntityType.Builder.of(::Spore, MobCategory.MISC))
    @JvmField val MELON: EntityType<Melon> = registerProjectile("melon", EntityType.Builder.of(::Melon, MobCategory.MISC), width = 1.0f, height = 0.8f)
    // endregion

    //region Other
    @JvmField val PLANT_POT_MINECART: EntityType<PlantPotMinecart> = register(
        "plant_pot_minecart",
        EntityType.Builder.of(::PlantPotMinecart, MobCategory.MISC)
            .noLootTable()
            .sized(0.98F, 0.7F)
            .passengerAttachments(0.75F)
            .clientTrackingRange(8)
    )
    @JvmField val SUN: EntityType<Sun> = register(
        "sun",
        EntityType.Builder.of(::Sun, MobCategory.MISC)
            .noLootTable()
            .sized(0.15F, 0.15F)
            .clientTrackingRange(6)
            .updateInterval(20)
    )
    // endregion

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

    private fun <T : Zombie> registerZombie(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.MONSTER),
        attributes: AttributeSupplier.Builder = Zombie.createAttributes()
    ): EntityType<T> {
        val type = register(name, builder)
        FabricDefaultAttributeRegistry.register(type, attributes)
        return type
    }

    private fun <T : Gnome> registerGnome(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.MONSTER),
        attributes: AttributeSupplier.Builder = createMobAttributes()
    ): EntityType<T> {
        val type = register(name, builder)
        FabricDefaultAttributeRegistry.register(type, attributes)
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

    lateinit var PLANT_TEAM : PlayerTeam
}