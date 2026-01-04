package joshxviii.plantz.model

import joshxviii.plantz.PazMain.MODID
import joshxviii.plantz.PlantRenderState
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.block.ModelBlockRenderer
import org.apache.logging.log4j.LogManager


open class PlantModel(
    root: ModelPart
) : EntityModel<PlantRenderState>(root) {

    override fun setupAnim(state: PlantRenderState) {
        super.setupAnim(state)
    }

}