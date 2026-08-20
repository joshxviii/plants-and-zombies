package joshxviii.plantz

import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.block.entity.GardenGnomeBlockEntity
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.model.BalloonModel
import joshxviii.plantz.model.FlagBlockModel
import joshxviii.plantz.model.GnomeArmorModel
import joshxviii.plantz.model.GnomeModel
import joshxviii.plantz.model.blueprint_machines.ZombieDroneModel
import joshxviii.plantz.model.blueprint_machines.ElectroTurretModel
import joshxviii.plantz.model.blueprint_machines.LawnMowerModel
import joshxviii.plantz.model.blueprint_machines.ZombieTurretModel
import joshxviii.plantz.model.plants.*
import joshxviii.plantz.model.projectiles.*
import joshxviii.plantz.model.zombies.*
import joshxviii.plantz.renderer.entity.BalloonRenderer
import joshxviii.plantz.renderer.FlagRenderState
import joshxviii.plantz.renderer.FlagRenderer
import joshxviii.plantz.renderer.GardenGnomeBlockRenderState
import joshxviii.plantz.renderer.GardenGnomeBlockRenderer
import joshxviii.plantz.renderer.MailboxRenderState
import joshxviii.plantz.renderer.entity.GnomeRenderer
import joshxviii.plantz.renderer.entity.PazZombieRenderer
import joshxviii.plantz.renderer.PlantPotMinecartRenderer
import joshxviii.plantz.renderer.entity.ProjectileRenderer
import joshxviii.plantz.renderer.SunBatteryRenderSate
import joshxviii.plantz.renderer.SunBatteryRenderer
import joshxviii.plantz.renderer.entity.SunRenderer
import joshxviii.plantz.renderer.TimeMachineRenderSate
import joshxviii.plantz.renderer.MailboxRenderer
import joshxviii.plantz.renderer.TimeMachineRenderer
import joshxviii.plantz.renderer.entity.GargantuarRenderer
import joshxviii.plantz.renderer.entity.PirateCaptainRenderer
import joshxviii.plantz.renderer.entity.PlantRenderer
import joshxviii.plantz.renderer.entity.RoboZombieRenderer
import joshxviii.plantz.renderer.entity.SuperBrainzRenderer
import joshxviii.plantz.renderer.entity.BlueprintMachineRenderer
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.resources.Identifier

object PazModels {

    @JvmField
    val HAS_HYPNO_KEY: RenderStateDataKey<Boolean> = RenderStateDataKey.create { "plantz:hypnotized" }
    @JvmField
    val HAS_FREEZE_KEY: RenderStateDataKey<Boolean> = RenderStateDataKey.create { "plantz:frozen" }
    @JvmField
    val PAINT_COLORS_KEY: RenderStateDataKey<Map<Int, Int>> = RenderStateDataKey.create { "plantz:painted" }

    private val PAINT_OVERLAY_TEXTURE_1: Identifier = pazResource("textures/gui/overlay/paint1.png")
    private val PAINT_OVERLAY_TEXTURE_2: Identifier = pazResource("textures/gui/overlay/paint2.png")
    private val PAINT_OVERLAY_TEXTURE_3: Identifier = pazResource("textures/gui/overlay/paint3.png")
    private val PAINT_OVERLAY_TEXTURE_4: Identifier = pazResource("textures/gui/overlay/paint4.png")
    private val PAINT_OVERLAY_TEXTURE_5: Identifier = pazResource("textures/gui/overlay/paint5.png")
    fun getOverlayTexture(value: Float): Identifier {
        return when {
            value < 0.2 -> PAINT_OVERLAY_TEXTURE_1
            value < 0.4 -> PAINT_OVERLAY_TEXTURE_2
            value < 0.6 -> PAINT_OVERLAY_TEXTURE_3
            value < 0.8 -> PAINT_OVERLAY_TEXTURE_4
            else -> PAINT_OVERLAY_TEXTURE_5
        }
    }

    val EMPTY_LAYER: ModelLayerLocation = ModelLayerLocation(pazResource("empty"), "empty")

    val EMPTY_ARMOR_SET = ArmorModelSet(
        EMPTY_LAYER,
        EMPTY_LAYER,
        EMPTY_LAYER,
        EMPTY_LAYER
    )

    val ARMOR_LAYER_HEAD   = ModelLayerLocation(pazResource("gnome_armor"), "head")
    val ARMOR_LAYER_CHEST  = ModelLayerLocation(pazResource("gnome_armor"), "chest")
    val ARMOR_LAYER_LEGS   = ModelLayerLocation(pazResource("gnome_armor"), "legs")
    val ARMOR_LAYER_FEET   = ModelLayerLocation(pazResource("gnome_armor"), "boots")

    val ARMOR_LAYER_LOCATION = GnomeArmorSet(
        head  = ARMOR_LAYER_HEAD,
        chest = ARMOR_LAYER_CHEST,
        legs  = ARMOR_LAYER_LEGS,
        feet  = ARMOR_LAYER_FEET
    )

    fun registerAll() {
        ModelLayerRegistry.registerModelLayer(EMPTY_LAYER) { LayerDefinition.create(MeshDefinition(), 0, 0) }


        // REGISTER MODELS
        ModelLayerRegistry.registerModelLayer(PeaShooterModel.LAYER_LOCATION) { PeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunflowerModel.LAYER_LOCATION) { SunflowerModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(WallNutModel.LAYER_LOCATION) { WallNutModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ChomperModel.LAYER_LOCATION) { ChomperModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CherryBombModel.LAYER_LOCATION) { CherryBombModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PotatoMineModel.LAYER_LOCATION) { PotatoMineModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(RepeaterModel.LAYER_LOCATION) { RepeaterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(IcePeaShooterModel.LAYER_LOCATION) { IcePeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(FirePeaShooterModel.LAYER_LOCATION) { FirePeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ElectricPeaShooterModel.LAYER_LOCATION) { ElectricPeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CactusModel.LAYER_LOCATION) { CactusModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(LightningReedModel.LAYER_LOCATION) { LightningReedModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CabbagePultModel.LAYER_LOCATION) { CabbagePultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(KernelPultModel.LAYER_LOCATION) { KernelPultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(MelonPultModel.LAYER_LOCATION) { MelonPultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(BonkChoyModel.LAYER_LOCATION) { BonkChoyModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(TangleKelpModel.LAYER_LOCATION) { TangleKelpModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PuffShroomModel.LAYER_LOCATION) { PuffShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ScaredyShroomModel.LAYER_LOCATION) { ScaredyShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(FumeShroomModel.LAYER_LOCATION) { FumeShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunShroomModel.LAYER_LOCATION) { SunShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunShroomBabyModel.LAYER_LOCATION) { SunShroomBabyModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(HypnoShroomModel.LAYER_LOCATION) { HypnoShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(DoomShroomModel.LAYER_LOCATION) { DoomShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SeaShroomModel.LAYER_LOCATION) { SeaShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CoffeeBeanModel.LAYER_LOCATION) { CoffeeBeanModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(GraveBusterModel.LAYER_LOCATION) { GraveBusterModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(PeaModel.LAYER_LOCATION) { PeaModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(NeedleModel.LAYER_LOCATION) { NeedleModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CabbageModel.LAYER_LOCATION) { CabbageModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(KernelModel.LAYER_LOCATION) { KernelModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ButterModel.LAYER_LOCATION) { ButterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(MelonModel.LAYER_LOCATION) { MelonModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SmallProjectileModel.LAYER_LOCATION) { SmallProjectileModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(LaserBulletModel.LAYER_LOCATION) { LaserBulletModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(MissileModel.LAYER_LOCATION) { MissileModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(PazZombieModel.LAYER_LOCATION) { PazZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(DiggerZombieModel.LAYER_LOCATION) { DiggerZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(EngineerZombieModel.LAYER_LOCATION) { EngineerZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ZombieYetiModel.LAYER_LOCATION) { ZombieYetiModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(DiscoZombieModel.LAYER_LOCATION) { DiscoZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(AllStarModel.LAYER_LOCATION) { AllStarModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SoldierZombieModel.LAYER_LOCATION) { SoldierZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PirateCaptainModel.LAYER_LOCATION) { PirateCaptainModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PirateCaptainGhostModel.LAYER_LOCATION) { PirateCaptainGhostModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(RoboZombieModel.LAYER_LOCATION) { RoboZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SuperBrainzModel.LAYER_LOCATION) { SuperBrainzModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ImpModel.LAYER_LOCATION) { ImpModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(GargantuarModel.LAYER_LOCATION) { GargantuarModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(ZombieTurretModel.LAYER_LOCATION) { ZombieTurretModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ElectroTurretModel.LAYER_LOCATION) { ElectroTurretModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ZombieDroneModel.LAYER_LOCATION) { ZombieDroneModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(LawnMowerModel.LAYER_LOCATION) { LawnMowerModel.createBodyLayer() }


        ModelLayerRegistry.registerModelLayer(GnomeModel.LAYER_LOCATION) { GnomeModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.head)  { GnomeArmorModel.createHeadLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.chest) { GnomeArmorModel.createChestLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.legs)  { GnomeArmorModel.createLegsLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.feet)  { GnomeArmorModel.createBootsLayer() }

        ModelLayerRegistry.registerModelLayer(BalloonModel.LAYER_LOCATION) { BalloonModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(FlagBlockModel.LAYER_LOCATION) { FlagBlockModel.createBodyLayer() }


        // REGISTER ENTITY RENDERERS
        EntityRenderers.register(PazEntities.PEA_SHOOTER) { PlantRenderer(PeaShooterModel(it.bakeLayer(PeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SUNFLOWER) { PlantRenderer(SunflowerModel(it.bakeLayer(SunflowerModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.WALL_NUT) { PlantRenderer(WallNutModel(it.bakeLayer(WallNutModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.EXPLODE_O_NUT) { PlantRenderer(WallNutModel(it.bakeLayer(WallNutModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CHOMPER) {
            PlantRenderer(
                ChomperModel(it.bakeLayer(ChomperModel.LAYER_LOCATION)),
                it
            )
        }
        EntityRenderers.register(PazEntities.CHERRY_BOMB) {
            PlantRenderer(
                CherryBombModel(it.bakeLayer(CherryBombModel.LAYER_LOCATION)),
                it
            )
        }
        EntityRenderers.register(PazEntities.POTATO_MINE) { PlantRenderer(PotatoMineModel(it.bakeLayer(PotatoMineModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.REPEATER) { PlantRenderer(RepeaterModel(it.bakeLayer(RepeaterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.ICE_PEA_SHOOTER) { PlantRenderer(IcePeaShooterModel(it.bakeLayer(IcePeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.FIRE_PEA_SHOOTER) { PlantRenderer(FirePeaShooterModel(it.bakeLayer(FirePeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.ELECTRIC_PEA_SHOOTER) { PlantRenderer(ElectricPeaShooterModel(it.bakeLayer(ElectricPeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CACTUS) { PlantRenderer(CactusModel(it.bakeLayer(CactusModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.LIGHTNING_REED) { PlantRenderer(LightningReedModel(it.bakeLayer(LightningReedModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CABBAGE_PULT) { PlantRenderer(CabbagePultModel(it.bakeLayer(CabbagePultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.KERNEL_PULT) { PlantRenderer(KernelPultModel(it.bakeLayer(KernelPultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.MELON_PULT) { PlantRenderer(MelonPultModel(it.bakeLayer(MelonPultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.BONK_CHOY) { PlantRenderer(BonkChoyModel(it.bakeLayer(BonkChoyModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.TANGLE_KELP) { PlantRenderer(TangleKelpModel(it.bakeLayer(TangleKelpModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PUFF_SHROOM) { PlantRenderer(PuffShroomModel(it.bakeLayer(PuffShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SCAREDY_SHROOM) { PlantRenderer(ScaredyShroomModel(it.bakeLayer(ScaredyShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.FUME_SHROOM) { PlantRenderer(FumeShroomModel(it.bakeLayer(FumeShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.HYPNOSHROOM) { PlantRenderer(HypnoShroomModel(it.bakeLayer(HypnoShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.DOOM_SHROOM) { PlantRenderer(DoomShroomModel(it.bakeLayer(DoomShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SEA_SHROOM) { PlantRenderer(SeaShroomModel(it.bakeLayer(SeaShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.COFFEE_BEAN) { PlantRenderer(CoffeeBeanModel(it.bakeLayer(CoffeeBeanModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.GRAVE_BUSTER) { PlantRenderer(GraveBusterModel(it.bakeLayer(GraveBusterModel.LAYER_LOCATION)), it) }

        EntityRenderers.register(PazEntities.SUN_SHROOM) { PlantRenderer(SunShroomModel(it.bakeLayer(SunShroomModel.LAYER_LOCATION)), it, SunShroomBabyModel(it.bakeLayer(SunShroomBabyModel.LAYER_LOCATION))) }


        EntityRenderers.register(PazEntities.PEA) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PEA_ICE) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PEA_FIRE) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PEA_ELECTRIC) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SPORE) { ProjectileRenderer(SmallProjectileModel(it.bakeLayer(SmallProjectileModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.WATER_SPORE) { ProjectileRenderer(SmallProjectileModel(it.bakeLayer(SmallProjectileModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.NEEDLE) { ProjectileRenderer(NeedleModel(it.bakeLayer(NeedleModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CABBAGE) { ProjectileRenderer(CabbageModel(it.bakeLayer(CabbageModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.KERNEL) { ProjectileRenderer(KernelModel(it.bakeLayer(KernelModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.BUTTER) { ProjectileRenderer(ButterModel(it.bakeLayer(ButterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.MELON) { ProjectileRenderer(MelonModel(it.bakeLayer(MelonModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PAINT_BALL) { ProjectileRenderer(SmallProjectileModel(it.bakeLayer(SmallProjectileModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.LASER_BULLET) { ProjectileRenderer(LaserBulletModel(it.bakeLayer(LaserBulletModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.MISSILE) { ProjectileRenderer(MissileModel(it.bakeLayer(MissileModel.LAYER_LOCATION)), it) }

        EntityRenderers.register(PazEntities.BROWN_COAT) { PazZombieRenderer(it) }
        EntityRenderers.register(PazEntities.NEWSPAPER_ZOMBIE) { PazZombieRenderer(it) }
        EntityRenderers.register(PazEntities.DIGGER_ZOMBIE) { PazZombieRenderer(it, DiggerZombieModel(it.bakeLayer(DiggerZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ENGINEER_ZOMBIE) { PazZombieRenderer(it, EngineerZombieModel(it.bakeLayer(EngineerZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ZOMBIE_YETI) { PazZombieRenderer(it, ZombieYetiModel(it.bakeLayer(ZombieYetiModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.BACKUP_DANCER) { PazZombieRenderer(it, DiscoZombieModel(it.bakeLayer(DiscoZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.DISCO_ZOMBIE) { PazZombieRenderer(it, DiscoZombieModel(it.bakeLayer(DiscoZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ALL_STAR) { PazZombieRenderer(it, AllStarModel(it.bakeLayer(AllStarModel.LAYER_LOCATION)), AllStarModel(it.bakeLayer(AllStarModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.SOLDIER_ZOMBIE) { PazZombieRenderer(it, SoldierZombieModel(it.bakeLayer(SoldierZombieModel.LAYER_LOCATION)), SoldierZombieModel(it.bakeLayer(SoldierZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.PIRATE_CAPTAIN) { PirateCaptainRenderer(it) }
        EntityRenderers.register(PazEntities.PIRATE_CAPTAIN_GHOST) { PirateCaptainRenderer(it, PirateCaptainGhostModel(it.bakeLayer(PirateCaptainGhostModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ROBO_ZOMBIE) { RoboZombieRenderer(it) }
        EntityRenderers.register(PazEntities.SUPER_BRAINZ) { SuperBrainzRenderer(it) }
        EntityRenderers.register(PazEntities.IMP) { PazZombieRenderer(it, ImpModel(it.bakeLayer(ImpModel.LAYER_LOCATION)), ImpModel(it.bakeLayer(ImpModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.GARGANTUAR) { GargantuarRenderer(it) }

        EntityRenderers.register(PazEntities.ZOMBIE_TURRET) { BlueprintMachineRenderer(it) }
        EntityRenderers.register(PazEntities.ELECTRO_TURRET) { BlueprintMachineRenderer(it, ElectroTurretModel(it.bakeLayer(ElectroTurretModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ZOMBIE_DRONE) { BlueprintMachineRenderer(it, ZombieDroneModel(it.bakeLayer(ZombieDroneModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.LAWN_MOWER) { BlueprintMachineRenderer(it, LawnMowerModel(it.bakeLayer(LawnMowerModel.LAYER_LOCATION))) }

        EntityRenderers.register(PazEntities.GNOME) { GnomeRenderer(it, GnomeModel(it.bakeLayer(GnomeModel.LAYER_LOCATION))) }

        EntityRenderers.register(PazEntities.PLANT_POT_MINECART) { PlantPotMinecartRenderer(it, ModelLayers.MINECART) }
        EntityRenderers.register(PazEntities.SUN) { SunRenderer(it) }
        EntityRenderers.register(PazEntities.THROWN_SUN_BOTTLE) { ThrownItemRenderer(it) }
        EntityRenderers.register(PazEntities.BALLOON) { BalloonRenderer(it) }

        BlockEntityRenderers.register<FlagBlockEntity, FlagRenderState>(PazBlocks.FLAG_BLOCK_ENTITY) { FlagRenderer(FlagBlockModel(it.bakeLayer(FlagBlockModel.LAYER_LOCATION))) }
        BlockEntityRenderers.register<GardenGnomeBlockEntity, GardenGnomeBlockRenderState>(PazBlocks.GARDEN_GNOME_ENTITY) { GardenGnomeBlockRenderer(GnomeModel(it.bakeLayer(GnomeModel.LAYER_LOCATION))) }
        BlockEntityRenderers.register<SunBatteryBlockEntity, SunBatteryRenderSate>(PazBlocks.SUN_BATTERY_BLOCK_ENTITY) { SunBatteryRenderer() }
        BlockEntityRenderers.register<TimeMachineBlockEntity, TimeMachineRenderSate>(PazBlocks.TIME_MACHINE_ENTITY) { TimeMachineRenderer() }
        BlockEntityRenderers.register<MailboxBlockEntity, MailboxRenderState>(PazBlocks.MAILBOX_ENTITY) { MailboxRenderer() }
    }
}