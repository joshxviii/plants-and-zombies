package joshxviii.plantz.model

import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*

class PeaShooterModel(
    root: ModelPart
) : PlantModel(root) {
    companion object {
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val stem = partdefinition.addOrReplaceChild(
                "stem",
                CubeListBuilder.create().texOffs(4, 25)
                    .addBox(-1.0f, -5.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val stem_2 = stem.addOrReplaceChild(
                "stem_2",
                CubeListBuilder.create().texOffs(4, 18)
                    .addBox(-1.0f, -5.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(2, 12).addBox(-2.0f, -6.0f, -2.0f, 4.0f, 2.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val head = stem_2.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -6.0f, -3.0f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(19, 19).addBox(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val barrel: PartDefinition? = head.addOrReplaceChild(
                "barrel",
                CubeListBuilder.create().texOffs(18, 25)
                    .addBox(-2.5f, -2.5f, -2.0f, 5.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, -5.0f)
            )

            val head_leaf =
                head.addOrReplaceChild("head_leaf", CubeListBuilder.create(), PartPose.offset(0.0f, -6.0f, 2.9167f))

            val cube_r1: PartDefinition? = head_leaf.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(30, -2)
                    .addBox(0.0f, -4.0f, 0.0f, 0.0f, 8.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0833f, 0.0f, 0.0f, 1.5708f)
            )

            val head_leaf2 = head_leaf.addOrReplaceChild(
                "head_leaf2",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 0.0f, 3.0833f)
            )

            val cube_r2: PartDefinition? = head_leaf2.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(36, -6)
                    .addBox(0.0f, -4.0f, 0.0f, 0.0f, 8.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5708f)
            )

            val leaves =
                partdefinition.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val leaf_1 = leaves.addOrReplaceChild(
                "leaf_1",
                CubeListBuilder.create().texOffs(31, 10)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.7071f, 0.0f, -0.6464f, 0.0f, -0.7854f, 0.0f)
            )

            val leaf_seg_1: PartDefinition? = leaf_1.addOrReplaceChild(
                "leaf_seg_1",
                CubeListBuilder.create().texOffs(31, 16)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val leaf_2 = partdefinition.addOrReplaceChild(
                "leaf_2",
                CubeListBuilder.create().texOffs(31, 10)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.7071f, 24.0f, 0.7678f, 0.0f, -2.3562f, 0.0f)
            )

            val leaf_seg_2: PartDefinition? = leaf_2.addOrReplaceChild(
                "leaf_seg_2",
                CubeListBuilder.create().texOffs(31, 16)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val leaf_3 = partdefinition.addOrReplaceChild(
                "leaf_3",
                CubeListBuilder.create().texOffs(31, 10)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.7071f, 24.0f, -0.6464f, 0.0f, 0.7854f, 0.0f)
            )

            val leaf_seg_3: PartDefinition? = leaf_3.addOrReplaceChild(
                "leaf_seg_3",
                CubeListBuilder.create().texOffs(31, 16)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            val leaf_4 = partdefinition.addOrReplaceChild(
                "leaf_4",
                CubeListBuilder.create().texOffs(31, 10)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.7071f, 24.0f, 0.7678f, 0.0f, 2.3562f, 0.0f)
            )

            val leaf_seg_4: PartDefinition? = leaf_4.addOrReplaceChild(
                "leaf_seg_4",
                CubeListBuilder.create().texOffs(31, 16)
                    .addBox(-3.0f, 0.0f, -6.0f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -6.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}