package joshxviii.plantz

import joshxviii.plantz.PazTags.EntityTypes.ATTACKS_PLANTS
import joshxviii.plantz.PazTags.EntityTypes.IGNORED_BY_PLANT_ATTACKERS
import joshxviii.plantz.PazTags.EntityTypes.ZOMBIE_RAIDERS
import joshxviii.plantz.ai.goal.DestroyFlagGoal
import joshxviii.plantz.ai.goal.PathfindToFlagGoal
import joshxviii.plantz.entity.Balloon
import joshxviii.plantz.entity.PlantPotMinecart
import joshxviii.plantz.entity.Sun
import joshxviii.plantz.entity.blueprint_machines.ElectroTurret
import joshxviii.plantz.entity.blueprint_machines.LawnMower
import joshxviii.plantz.entity.blueprint_machines.ZombieDrone
import joshxviii.plantz.entity.blueprint_machines.ZombieTurret
import joshxviii.plantz.entity.gnome.Gnome
import joshxviii.plantz.entity.plant.*
import joshxviii.plantz.entity.projectile.*
import joshxviii.plantz.entity.zombie.*
import joshxviii.plantz.mixin.MobAccessor
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
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
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin
import net.minecraft.world.entity.projectile.Projectile

object PazEntities {

    fun initialize() {

        ServerEntityEvents.ENTITY_LOAD.register { entity, level ->

            if (entity is Zombie) (entity as MobAccessor).targetSelector.addGoal(4, NearestAttackableTargetGoal(entity, Gnome::class.java, 5, true, false, null))

            if (entity is PathfinderMob && entity.`is`(ZOMBIE_RAIDERS)) {
                (entity as MobAccessor).goalSelector.addGoal(2, DestroyFlagGoal(entity))
                (entity as MobAccessor).goalSelector.addGoal(3, PathfindToFlagGoal(entity))
            }

            if (entity is Mob && entity.`is`(ATTACKS_PLANTS) && entity !is ZombifiedPiglin) {
                (entity as MobAccessor).targetSelector.addGoal(2, NearestAttackableTargetGoal(entity, WallNut::class.java, 6, true, true) { target, level -> ((target as? WallNut ?: target as? ExplodeONut)?.let { it.distanceToSqr(entity) < 3.5 } ?: false)})
                (entity as MobAccessor).targetSelector.addGoal(3, NearestAttackableTargetGoal(entity, Plant::class.java, 5, true, false) { target, level ->
                    target !is WallNut && !target.`is`(IGNORED_BY_PLANT_ATTACKERS) })
            }
        }
    }

    // region Plants
    @JvmField val SUNFLOWER: EntityType<Sunflower> = registerPlant(
        "sunflower",
        EntityType.Builder.of(::Sunflower, MobCategory.CREATURE),
        height = 1.1f,
    )
    @JvmField val PEA_SHOOTER: EntityType<PeaShooter> = registerPlant(
        "peashooter",
        EntityType.Builder.of(::PeaShooter, MobCategory.CREATURE),
    )
    @JvmField val WALL_NUT: EntityType<WallNut> = registerPlant(
        "wallnut",
        EntityType.Builder.of(::WallNut, MobCategory.CREATURE),
        width = 1.0f,
        height = 1.15f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 60.0,
        )
    )
    @JvmField val EXPLODE_O_NUT: EntityType<ExplodeONut> = registerPlant(
        "explode_o_nut",
        EntityType.Builder.of(::ExplodeONut, MobCategory.CREATURE),
        width = 1.0f,
        height = 1.15f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 60.0,
        )
    )
    @JvmField val CHOMPER: EntityType<Chomper> = registerPlant(
        "chomper",
        EntityType.Builder.of(::Chomper, MobCategory.CREATURE),
        height = 1.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 25.0,
            attackDamage = 10.0,
            attackKnockback = 0.15,
            attackRange = 3.0,
            followRange = 4.75,
        )
    )
    @JvmField val CHERRY_BOMB: EntityType<CherryBomb> = registerPlant(
        "cherrybomb",
        EntityType.Builder.of(::CherryBomb, MobCategory.CREATURE),
        width = 0.625f,
        height = 0.75f,
        attributes = Plant.Companion.PlantAttributes(
            followRange = 3.75,
        )
    )
    @JvmField val POTATO_MINE: EntityType<PotatoMine> = registerPlant(
        "potatomine",
        EntityType.Builder.of(::PotatoMine, MobCategory.CREATURE),
        width = 0.65f,
        height = 0.35f,
        attributes = Plant.Companion.PlantAttributes(
            followRange = 3.75,
        )
    )
    @JvmField val REPEATER: EntityType<Repeater> = registerPlant(
        "repeater",
        EntityType.Builder.of(::Repeater, MobCategory.CREATURE),
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.0,
        )
    )
    @JvmField val ICE_PEA_SHOOTER: EntityType<IcePeaShooter> = registerPlant(
        "ice_peashooter",
        EntityType.Builder.of(::IcePeaShooter, MobCategory.CREATURE),
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.0,
        )
    )
    @JvmField val FIRE_PEA_SHOOTER: EntityType<FirePeaShooter> = registerPlant(
        "fire_peashooter",
        EntityType.Builder.of(::FirePeaShooter, MobCategory.CREATURE).fireImmune(),
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.25,
        )
    )
    @JvmField val ELECTRIC_PEA_SHOOTER: EntityType<ElectricPeaShooter> = registerPlant(
        "electric_peashooter",
        EntityType.Builder.of(::ElectricPeaShooter, MobCategory.CREATURE),
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.0,
        )
    )
    @JvmField val CACTUS: EntityType<Cactus> = registerPlant(
        "cactus",
        EntityType.Builder.of(::Cactus, MobCategory.CREATURE),
        width = 0.8f,
        height = 1.25f,
        eyeHeight = 0.85f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.5,
            followRange = 34.0,
        )
    )
    @JvmField val LIGHTNING_REED: EntityType<LightningReed> = registerPlant(
        "lightning_reed",
        EntityType.Builder.of(::LightningReed, MobCategory.CREATURE),
        width = 0.4f,
        height = 1.0f,
        eyeHeight = 0.7f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.5,
            attackRange = 7.25,
            followRange = 6.5,
        )
    )
    @JvmField val CABBAGE_PULT: EntityType<CabbagePult> = registerPlant(
        "cabbagepult",
        EntityType.Builder.of(::CabbagePult, MobCategory.CREATURE),
        width = 0.9f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 2.0,
            attackKnockback = 0.5,
            followRange = 22.0,
        )
    )
    @JvmField val KERNEL_PULT: EntityType<KernelPult> = registerPlant(
        "kernelpult",
        EntityType.Builder.of(::KernelPult, MobCategory.CREATURE),
        width = 0.9f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 1.5,
            attackKnockback = 0.5,
            followRange = 26.0,
        )
    )
    @JvmField val MELON_PULT: EntityType<MelonPult> = registerPlant(
        "melonpult",
        EntityType.Builder.of(::MelonPult, MobCategory.CREATURE),
        width = 0.9f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 35.0,
            attackDamage = 3.0,
            followRange = 38.0,
        )
    )
    @JvmField val BONK_CHOY: EntityType<BonkChoy> = registerPlant(
        "bonkchoy",
        EntityType.Builder.of(::BonkChoy, MobCategory.CREATURE),
        height = 0.8f,
        eyeHeight = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 4.0,
            attackKnockback = 0.45,
            attackRange = 2.5,
            followRange = 4.0,
        )
    )
    @JvmField val TANGLE_KELP: EntityType<TangleKelp> = registerPlant(
        "tanglekelp", EntityType.Builder.of(::TangleKelp, MobCategory.CREATURE),
        width = 1.0f,
        height = 0.4f,
        eyeHeight = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 25.0,
            attackDamage = 0.75,
            attackKnockback = 0.0,
            attackRange = 4.5,
            followRange = 5.75,
            scale = 1.25
        )
    )
    @JvmField val PUFF_SHROOM: EntityType<PuffShroom> = registerPlant(
        "puffshroom", EntityType.Builder.of(::PuffShroom, MobCategory.CREATURE),
        width = 0.5f,
        height = 0.65f,
        eyeHeight = 0.3f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 12.0,
            attackDamage = 0.5,
            followRange = 10.0,
        )
    )
    @JvmField val SCAREDY_SHROOM: EntityType<ScaredyShroom> = registerPlant(
        "scaredyshroom", EntityType.Builder.of(::ScaredyShroom, MobCategory.CREATURE),
        width = 0.5f,
        height = 0.9f,
        eyeHeight = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 18.0,
            followRange = 22.0,
            attackDamage = 1.0,
        )
    )
    @JvmField val FUME_SHROOM: EntityType<FumeShroom> = registerPlant(
        "fumeshroom",
        EntityType.Builder.of(::FumeShroom, MobCategory.CREATURE),
        width = 0.8f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            attackDamage = 2.0,
        )
    )
    @JvmField val SUN_SHROOM: EntityType<SunShroom> = registerPlant(
        "sunshroom",
        EntityType.Builder.of(::SunShroom, MobCategory.CREATURE),
        height = 0.85f
    )
    @JvmField val HYPNOSHROOM: EntityType<HypnoShroom> = registerPlant(
        "hypnoshroom", EntityType.Builder.of(::HypnoShroom, MobCategory.CREATURE),
        width = 0.6f,
        height = 1.3f,
        eyeHeight = 0.6f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 4.0,
            followRange = 20.0
        )
    )
    @JvmField val DOOM_SHROOM: EntityType<DoomShroom> = registerPlant(
        "doomshroom", EntityType.Builder.of(::DoomShroom, MobCategory.CREATURE),
        eyeHeight = 0.6f,
        height = 0.8f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 28.0,
            followRange = 5.0
        )
    )
    @JvmField val SEA_SHROOM: EntityType<SeaShroom> = registerPlant(
        "seashroom", EntityType.Builder.of(::SeaShroom, MobCategory.CREATURE),
        width = 0.5f,
        height = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 12.0,
            attackDamage = 0.5,
            followRange = 10.0,
        )
    )
    @JvmField val COFFEE_BEAN: EntityType<CoffeeBean> = registerPlant(
        "coffeebean", EntityType.Builder.of(::CoffeeBean, MobCategory.CREATURE),
        width = 0.4f,
        height = 0.5f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 4.0,
            followRange = 1.0
        )
    )
    @JvmField val GRAVE_BUSTER: EntityType<GraveBuster> = registerPlant(
        "grave_buster", EntityType.Builder.of(::GraveBuster, MobCategory.CREATURE),
        width = 1.0f,
        height = 1.0f,
        eyeHeight = 0.6f,
        attributes = Plant.Companion.PlantAttributes(
            maxHealth = 6.0,
            followRange = 1.0
        )
    )
    // endregion

    // region Zombies
    @JvmField val BROWN_COAT: EntityType<BrownCoat> =  registerZombie(
        "browncoat",
        EntityType.Builder.of(::BrownCoat, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            spawnReinforcementsChance = 10.0
        )
    )
    @JvmField val NEWSPAPER_ZOMBIE: EntityType<NewspaperZombie> =  registerZombie(
        "newspaper_zombie",
        EntityType.Builder.of(::NewspaperZombie, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            movementSpeed = 0.22,
            maxHealth = 25.0,
        )
    )
    @JvmField val DIGGER_ZOMBIE: EntityType<DiggerZombie> =  registerZombie(
        "digger_zombie",
        EntityType.Builder.of(::DiggerZombie, MobCategory.MONSTER)
            .sized(0.63f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 2.0,
            movementSpeed = 0.225,
            maxHealth = 35.0,
            followRange = 26.0,
        )
    )
    @JvmField val ENGINEER_ZOMBIE: EntityType<EngineerZombie> =  registerZombie(
        "engineer_zombie",
        EntityType.Builder.of(::EngineerZombie, MobCategory.MONSTER)
            .sized(0.63f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            movementSpeed = 0.24,
            maxHealth = 35.0,
            followRange = 32.0,
        )
    )
    @JvmField val ZOMBIE_YETI: EntityType<ZombieYeti> =  registerZombie(
        "zombie_yeti",
        EntityType.Builder.of(::ZombieYeti, MobCategory.MONSTER)
            .sized(1.25f, 2.6f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            waterMovementEfficiency = 2.0,
            attackDamage = 13.0,
            maxHealth = 120.0,
            movementSpeed = 0.27,
            knockbackResistance = 0.5,
            scale = 1.25,
            stepHeight = 1.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val DISCO_ZOMBIE: EntityType<DiscoZombie> =  registerZombie(
        "disco_zombie",
        EntityType.Builder.of(::DiscoZombie, MobCategory.MONSTER)
            .sized(0.64f, 2.2f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 6.0,
            maxHealth = 60.0,
            movementSpeed = 0.235,
            spawnReinforcementsChance = 0.1,
        )
    )
    @JvmField val BACKUP_DANCER: EntityType<BackupDancer> =  registerZombie(
        "backup_dancer",
        EntityType.Builder.of(::BackupDancer, MobCategory.MONSTER)
            .sized(0.64f, 1.96f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 3.5,
            maxHealth = 20.0,
            movementSpeed = 0.3,
            followRange = 24.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val ALL_STAR: EntityType<AllStar> =  registerZombie(
        "all_star",
        EntityType.Builder.of(::AllStar, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 7.0,
            maxHealth = 75.0,
            stepHeight = 1.0,
            movementSpeed = 0.23,
            spawnReinforcementsChance = 1.5,
        )
    )
    @JvmField val SOLDIER_ZOMBIE: EntityType<SoldierZombie> = registerZombie(
        "soldier_zombie",
        EntityType.Builder.of(::SoldierZombie, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 4.0,
            maxHealth = 40.0,
            movementSpeed = 0.237,
            spawnReinforcementsChance = 2.0,
        )
    )
    @JvmField val PIRATE_CAPTAIN: EntityType<PirateCaptain> =  registerZombie(
        "pirate_captain",
        EntityType.Builder.of(::PirateCaptain, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            armor = 4.0,
            attackDamage = 6.0,
            maxHealth = 120.0,
            movementSpeed = 0.21,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val PIRATE_CAPTAIN_GHOST: EntityType<PirateCaptainGhost> =  registerZombie(
        "pirate_captain_ghost",
        EntityType.Builder.of(::PirateCaptainGhost, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 6.0,
            maxHealth = 80.0,
            movementSpeed = 0.25,
            followRange = 126.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val ROBO_ZOMBIE: EntityType<RoboZombie> =  registerZombie(
        "robo_zombie",
        EntityType.Builder.of(::RoboZombie, MobCategory.MONSTER)
            .sized(1.3f, 1.95f)
            .eyeHeight(1.74f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            armor = 16.0,
            attackDamage = 10.0,
            maxHealth = 100.0,
            stepHeight = 1.0,
            movementSpeed = 0.23,
            knockbackResistance = 1.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val SUPER_BRAINZ: EntityType<SuperBrainz> =  registerZombie(
        "super_brainz",
        EntityType.Builder.of(::SuperBrainz, MobCategory.MONSTER)
            .sized(1.25f, 2.4f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            armor = 8.0,
            attackDamage = 8.0,
            maxHealth = 150.0,
            stepHeight = 1.0,
            movementSpeed = 0.25,
            flyingSpeed = 0.2,
            knockbackResistance = 0.6,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val IMP: EntityType<Imp> =  registerZombie(
        "imp",
        EntityType.Builder.of(::Imp, MobCategory.MONSTER)
            .sized(0.45f, 0.95f)
            .passengerAttachments(2.075f)
            .ridingOffset(-0.7f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            attackDamage = 1.5,
            maxHealth = 20.0,
            movementSpeed = 0.32,
            spawnReinforcementsChance = 0.3,
        )

    )
    @JvmField val GARGANTUAR: EntityType<Gargantuar> =  registerZombie(
        "gargantuar",
        EntityType.Builder.of(::Gargantuar, MobCategory.MONSTER)
            .sized(1.7f, 3.2f)
            .passengerAttachments(2.0f)
            .clientTrackingRange(8),
        attributes = PazZombie.Companion.PazZombieAttributes(
            armor = 6.0,
            attackDamage = 8.0,
            maxHealth = 600.0,
            movementSpeed = 0.21,
            knockbackResistance = 1.4,
            explosionKnockbackResistance = 0.7,
            scale = 1.33,
            stepHeight = 1.0,
            attackRange = 2.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    // endregion

    @JvmField val ZOMBIE_TURRET: EntityType<ZombieTurret> = registerZombie(
        "zombie_turret",
        EntityType.Builder.of(::ZombieTurret, MobCategory.MISC).sized(0.8f, 1.0f),
        attributes = PazZombie.Companion.PazZombieAttributes(
            maxHealth = 8.0,
            armor = 7.0,
            followRange = 20.0,
            attackDamage = 1.5,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val ELECTRO_TURRET: EntityType<ElectroTurret> = registerZombie(
        "electro_turret",
        EntityType.Builder.of(::ElectroTurret, MobCategory.MISC).sized(0.8f, 1.0f),
        attributes = PazZombie.Companion.PazZombieAttributes(
            maxHealth = 8.0,
            armor = 7.0,
            attackRange = 7.75,
            followRange = 8.5,
            attackDamage = 2.0,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val ZOMBIE_DRONE: EntityType<ZombieDrone> = registerZombie(
        "zombie_drone",
        EntityType.Builder.of(::ZombieDrone, MobCategory.MISC).sized(0.8f, 1.0f),
        attributes = PazZombie.Companion.PazZombieAttributes(
            maxHealth = 8.0,
            armor = 7.0,
            followRange = 28.0,
            attackDamage = 3.0,
            flyingSpeed = 0.18,
            spawnReinforcementsChance = 0.0,
        )
    )
    @JvmField val LAWN_MOWER: EntityType<LawnMower> = registerOther(
        "lawn_mower",
        EntityType.Builder.of(::LawnMower, MobCategory.MISC).sized(0.8f, 0.6f),
    )

    @JvmField val GNOME: EntityType<Gnome> =  registerGnome(
        "gnome",
        EntityType.Builder.of(::Gnome, MobCategory.MONSTER)
            .sized(0.4f, 0.78f)
            .ridingOffset(-0.15f),
        attributes = createMobAttributes()
            .add(Attributes.STEP_HEIGHT, 1.0)
            .add(Attributes.MAX_HEALTH, 18.0)
            .add(Attributes.MOVEMENT_SPEED, 0.5)
            .add(Attributes.JUMP_STRENGTH, 0.4)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 2.5)
    )

    //region Projectiles
    @JvmField val THROWN_SUN_BOTTLE: EntityType<ThrownSunBottle> = registerProjectile("thrown_sun_bottle", EntityType.Builder.of({ _, l->ThrownSunBottle(l)}, MobCategory.MISC))
    @JvmField val PEA: EntityType<Pea> = registerProjectile("pea", EntityType.Builder.of({_,l->Pea(l)}, MobCategory.MISC))
    @JvmField val PEA_ICE: EntityType<PeaIce> = registerProjectile("pea_ice", EntityType.Builder.of({_,l->PeaIce(l)}, MobCategory.MISC))
    @JvmField val PEA_FIRE: EntityType<PeaFire> = registerProjectile("pea_fire", EntityType.Builder.of({_,l->PeaFire(l)}, MobCategory.MISC))
    @JvmField val PEA_ELECTRIC: EntityType<PeaElectric> = registerProjectile("pea_electric", EntityType.Builder.of({_,l-> PeaElectric(l)}, MobCategory.MISC))
    @JvmField val NEEDLE: EntityType<Needle> = registerProjectile("needle", EntityType.Builder.of({_,l->Needle(l)}, MobCategory.MISC), width = 0.42f, height = 0.42f)
    @JvmField val SPORE: EntityType<Spore> = registerProjectile("spore", EntityType.Builder.of({_,l->Spore(l)}, MobCategory.MISC))
    @JvmField val WATER_SPORE: EntityType<WaterSpore> = registerProjectile("water_spore", EntityType.Builder.of({_,l-> WaterSpore(l)}, MobCategory.MISC))
    @JvmField val CABBAGE: EntityType<Cabbage> = registerProjectile("cabbage", EntityType.Builder.of({_,l->Cabbage(l)}, MobCategory.MISC), width = 0.5f, height = 0.5f)
    @JvmField val KERNEL: EntityType<Kernel> = registerProjectile("kernel", EntityType.Builder.of({_,l->Kernel(l)}, MobCategory.MISC), width = 0.42f, height = 0.42f)
    @JvmField val BUTTER: EntityType<Butter> = registerProjectile("butter", EntityType.Builder.of({_,l->Butter(l)}, MobCategory.MISC), width = 0.75f, height = 0.5f)
    @JvmField val MELON: EntityType<Melon> = registerProjectile("melon", EntityType.Builder.of({_,l->Melon(l)}, MobCategory.MISC), width = 1.0f, height = 0.8f)
    @JvmField val PAINT_BALL: EntityType<PaintBall> = registerProjectile("paint_ball", EntityType.Builder.of({ _, l->PaintBall(l)}, MobCategory.MISC), width = 0.42f, height = 0.42f)
    @JvmField val LASER_BULLET: EntityType<LaserBullet> = registerProjectile("laser_bullet", EntityType.Builder.of({ _, l-> LaserBullet(l)}, MobCategory.MISC), width = 0.5f, height = 0.5f)
    @JvmField val MISSILE: EntityType<Missile> = registerProjectile("missile", EntityType.Builder.of({ _, l->Missile(l)}, MobCategory.MISC), width = 0.42f, height = 0.42f)
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
    @JvmField val BALLOON: EntityType<Balloon> = register(
        "balloon",
        EntityType.Builder.of(::Balloon, MobCategory.MISC)
            .noLootTable()
            .sized(0.6f, 0.7f)
            .clientTrackingRange(8)
    )
    @JvmField val SUN: EntityType<Sun> = register(
        "sun",
        EntityType.Builder.of(::Sun, MobCategory.MISC)
            .fireImmune()
            .noLootTable()
            .sized(0.3F, 0.3F)
            .clientTrackingRange(6)
            .updateInterval(20)
    )
    // endregion

    private fun <T : LivingEntity> registerPlant(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.CREATURE),
        width: Float = 0.6f,
        height: Float = 1.0f,
        eyeHeight: Float = height * 0.85f,
        attributes: Plant.Companion.PlantAttributes = Plant.Companion.PlantAttributes()
    ): EntityType<T> {
        builder.sized(width, height).eyeHeight(eyeHeight)
        val type = register(name, builder)
        FabricDefaultAttributeRegistry.register(type, attributes.apply(createMobAttributes()))
        return type
    }

    private fun <T : Zombie> registerZombie(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.MONSTER),
        attributes: PazZombie.Companion.PazZombieAttributes = PazZombie.Companion.PazZombieAttributes()
    ): EntityType<T> {
        val type = register(name, builder
            .ridingOffset(-0.7f)
            .notInPeaceful())
        FabricDefaultAttributeRegistry.register(type, attributes.apply(createMobAttributes()))
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

    private fun <T : LivingEntity> registerOther(
        name : String,
        builder: EntityType.Builder<T> = EntityType.Builder.createNothing(MobCategory.MISC),
        attributes: AttributeSupplier.Builder = createMobAttributes()
    ): EntityType<T> {
        val type = register(name, builder)
        FabricDefaultAttributeRegistry.register(type, attributes)
        return type
    }

    private fun <T : Entity> register(
        name : String,
        builder: EntityType.Builder<T>
    ): EntityType<T> {
        val id = ResourceKey.create(Registries.ENTITY_TYPE, pazResource(name))
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id))
    }

    val MAGIC_NAMES = mapOf<EntityType<*>, String>(
        CHOMPER      to "chester",
        DISCO_ZOMBIE to "mj",
        BROWN_COAT   to "tugboat"
    )
}