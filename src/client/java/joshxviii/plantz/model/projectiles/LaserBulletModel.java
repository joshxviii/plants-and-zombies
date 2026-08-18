package joshxviii.plantz.model.projectiles;

import joshxviii.plantz.renderer.entity.ProjectileRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class LaserBulletModel extends EntityModel<@NotNull ProjectileRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("laser_bullet"), "main");
    private final ModelPart body;

    public LaserBulletModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 4).addBox(-7.5F, -1.5F, -0.5F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition outline = body.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(26, 4).addBox(3.5F, 1.5F, 1.5F, -11.0F, -2.0F, -2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -1.5F, -0.5F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void setupAnim(final @NotNull ProjectileRenderState state) {
        super.setupAnim(state);
    }
}
