package joshxviii.plantz.model

import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*

class SunflowerModel(
    modelPart: ModelPart
) : PlantModel(modelPart) {
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
                    .addBox(-1.0f, -5.0f, -1.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val cube_r1: PartDefinition? = stem_2.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(25, 4).mirror()
                    .addBox(-5.0f, 0.0f, -2.5f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(25, 4).addBox(2.0f, 0.0f, -2.5f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, -1.0f, 0.0f, 0.7854f, 0.0f, 0.0f)
            )

            val stem_3 = stem_2.addOrReplaceChild(
                "stem_3",
                CubeListBuilder.create().texOffs(12, 18)
                    .addBox(-1.0f, -4.0f, -1.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.0f, 0.0f)
            )

            val head = stem_3.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.0f, -5.0071f, -1.9867f, 12.0f, 9.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 32).addBox(-9.0f, -9.0071f, 0.0133f, 18.0f, 17.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(3, 13).addBox(-2.0f, -2.0071f, 0.0133f, 4.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.9929f, -1.0133f)
            )

            val cube_r2: PartDefinition? = head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(29, 0)
                    .addBox(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -2.0071f, 0.0133f, -0.7854f, 0.0f, 0.0f)
            )

            val cube_r3: PartDefinition? = head.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(29, 0)
                    .addBox(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 1.9929f, 0.0133f, -2.3562f, 0.0f, 0.0f)
            )

            val cube_r4: PartDefinition? = head.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(29, 0)
                    .addBox(-3.0f, -4.4142f, -2.4142f, 6.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.0071f, 1.4275f, -0.7854f, 0.0f, -1.5708f)
            )

            val cube_r5: PartDefinition? = head.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(29, 0)
                    .addBox(-3.0f, -4.4142f, 2.4142f, 6.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -0.0071f, 1.4275f, -2.3562f, 0.0f, -1.5708f)
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