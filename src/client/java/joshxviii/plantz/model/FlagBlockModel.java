package joshxviii.plantz.model;

import joshxviii.plantz.FlagRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;

import static joshxviii.plantz.UtilsKt.pazResource;

/**
 * @author Josh
 */
public class FlagBlockModel extends Model<FlagRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("flag"), "main");
    private final ModelPart flag;

    public FlagBlockModel(final ModelPart root) {
        super(root, RenderTypes::entitySolid);
        this.flag = root.getChild("flag");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition flag = partdefinition.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -5.5F, 0.0F, 1.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 17.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(FlagRenderState state) {
        super.setupAnim(state);
        this.flag.yRot = (0.075F * Mth.cos(((float)Math.PI * 2F) * state.getPhase())) * (float)Math.PI;
    }
}
