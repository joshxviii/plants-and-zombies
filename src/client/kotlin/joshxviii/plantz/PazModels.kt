package joshxviii.plantz

import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.model.FlagBlockModel
import joshxviii.plantz.model.GnomeArmorModel
import joshxviii.plantz.model.GnomeModel
import joshxviii.plantz.model.plants.*
import joshxviii.plantz.model.projectiles.MelonModel
import joshxviii.plantz.model.projectiles.NeedleModel
import joshxviii.plantz.model.projectiles.PeaModel
import joshxviii.plantz.model.projectiles.SporeModel
import joshxviii.plantz.model.zombies.DiscoZombieModel
import joshxviii.plantz.model.zombies.GargantuarModel
import joshxviii.plantz.model.zombies.DiggerZombieModel
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.ZombieYetiModel
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.EntityRenderers

object PazModels {

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
        ModelLayerRegistry.registerModelLayer(IcePeaShooterModel.LAYER_LOCATION) { IcePeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(RepeaterModel.LAYER_LOCATION) { RepeaterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(FirePeaShooterModel.LAYER_LOCATION) { FirePeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CactusModel.LAYER_LOCATION) { CactusModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CabbagePultModel.LAYER_LOCATION) { CabbagePultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(KernelPultModel.LAYER_LOCATION) { KernelPultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(MelonPultModel.LAYER_LOCATION) { MelonPultModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PuffShroomModel.LAYER_LOCATION) { PuffShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ScaredyShroomModel.LAYER_LOCATION) { ScaredyShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(FumeShroomModel.LAYER_LOCATION) { FumeShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunShroomModel.LAYER_LOCATION) { SunShroomModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunShroomBabyModel.LAYER_LOCATION) { SunShroomBabyModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(HypnoShroomModel.LAYER_LOCATION) { HypnoShroomModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(PeaModel.LAYER_LOCATION) { PeaModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SporeModel.LAYER_LOCATION) { SporeModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(NeedleModel.LAYER_LOCATION) { NeedleModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(MelonModel.LAYER_LOCATION) { MelonModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(PazZombieModel.LAYER_LOCATION) { PazZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(DiggerZombieModel.LAYER_LOCATION) { DiggerZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ZombieYetiModel.LAYER_LOCATION) { ZombieYetiModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(DiscoZombieModel.LAYER_LOCATION) { DiscoZombieModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(GargantuarModel.LAYER_LOCATION) { GargantuarModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(GnomeModel.LAYER_LOCATION) { GnomeModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.head)  { GnomeArmorModel.createHeadLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.chest) { GnomeArmorModel.createChestLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.legs)  { GnomeArmorModel.createLegsLayer() }
        ModelLayerRegistry.registerModelLayer(ARMOR_LAYER_LOCATION.feet)  { GnomeArmorModel.createBootsLayer() }

        ModelLayerRegistry.registerModelLayer(FlagBlockModel.LAYER_LOCATION) { FlagBlockModel.createBodyLayer() }



        // REGISTER ENTITY RENDERERS
        EntityRenderers.register(PazEntities.PEA_SHOOTER) { PlantRenderer(PeaShooterModel(it.bakeLayer(PeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SUNFLOWER) { PlantRenderer(SunflowerModel(it.bakeLayer(SunflowerModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.WALL_NUT) { PlantRenderer(WallNutModel(it.bakeLayer(WallNutModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CHOMPER) {PlantRenderer(ChomperModel(it.bakeLayer(ChomperModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CHERRY_BOMB) { PlantRenderer(CherryBombModel(it.bakeLayer(CherryBombModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.POTATO_MINE) { PlantRenderer(PotatoMineModel(it.bakeLayer(PotatoMineModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.ICE_PEA_SHOOTER) { PlantRenderer(IcePeaShooterModel(it.bakeLayer(IcePeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.REPEATER) { PlantRenderer(RepeaterModel(it.bakeLayer(RepeaterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.FIRE_PEA_SHOOTER) { PlantRenderer(FirePeaShooterModel(it.bakeLayer(FirePeaShooterModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CACTUS) { PlantRenderer(CactusModel(it.bakeLayer(CactusModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.CABBAGE_PULT) { PlantRenderer(CabbagePultModel(it.bakeLayer(CabbagePultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.KERNEL_PULT) { PlantRenderer(KernelPultModel(it.bakeLayer(KernelPultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.MELON_PULT) { PlantRenderer(MelonPultModel(it.bakeLayer(MelonPultModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PUFF_SHROOM) { PlantRenderer(PuffShroomModel(it.bakeLayer(PuffShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SCAREDY_SHROOM) { PlantRenderer(ScaredyShroomModel(it.bakeLayer(ScaredyShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.FUME_SHROOM) { PlantRenderer(FumeShroomModel(it.bakeLayer(FumeShroomModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.HYPNOSHROOM) { PlantRenderer(HypnoShroomModel(it.bakeLayer(HypnoShroomModel.LAYER_LOCATION)), it) }

        EntityRenderers.register(PazEntities.SUN_SHROOM) { PlantRenderer(
            SunShroomModel(it.bakeLayer(SunShroomModel.LAYER_LOCATION)), it,
            SunShroomBabyModel(it.bakeLayer(SunShroomBabyModel.LAYER_LOCATION))) }


        EntityRenderers.register(PazEntities.PEA) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PEA_ICE) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.PEA_FIRE) { ProjectileRenderer(PeaModel(it.bakeLayer(PeaModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.SPORE) { ProjectileRenderer(SporeModel(it.bakeLayer(SporeModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.NEEDLE) { ProjectileRenderer(NeedleModel(it.bakeLayer(NeedleModel.LAYER_LOCATION)), it) }
        EntityRenderers.register(PazEntities.MELON) { ProjectileRenderer(MelonModel(it.bakeLayer(MelonModel.LAYER_LOCATION)), it) }

        EntityRenderers.register(PazEntities.BROWN_COAT) { PazZombieRenderer(it) }
        EntityRenderers.register(PazEntities.NEWSPAPER_ZOMBIE) { PazZombieRenderer(it) }
        EntityRenderers.register(PazEntities.DIGGER_ZOMBIE) { PazZombieRenderer(it, DiggerZombieModel(it.bakeLayer(DiggerZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.ZOMBIE_YETI) { PazZombieRenderer(it, ZombieYetiModel(it.bakeLayer(ZombieYetiModel.LAYER_LOCATION)))}
        EntityRenderers.register(PazEntities.BACKUP_DANCER) { PazZombieRenderer(it, DiscoZombieModel(it.bakeLayer(DiscoZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.DISCO_ZOMBIE) { PazZombieRenderer(it, DiscoZombieModel(it.bakeLayer(DiscoZombieModel.LAYER_LOCATION))) }
        EntityRenderers.register(PazEntities.GARGANTUAR) { PazZombieRenderer(it, GargantuarModel(it.bakeLayer(GargantuarModel.LAYER_LOCATION))) }

        EntityRenderers.register(PazEntities.GNOME) { GnomeRenderer(it, GnomeModel(it.bakeLayer(GnomeModel.LAYER_LOCATION)))}

        EntityRenderers.register(PazEntities.PLANT_POT_MINECART) { PlantPotMinecartRenderer(it, ModelLayers.MINECART) }
        EntityRenderers.register(PazEntities.SUN) { SunRenderer(it) }

        BlockEntityRenderers.register<FlagBlockEntity, FlagRenderState>(PazBlocks.FLAG_BLOCK_ENTITY) { FlagRenderer(FlagBlockModel(it.bakeLayer(FlagBlockModel.LAYER_LOCATION))) }
    }
}