package joshxviii.plantz

import joshxviii.plantz.model.GnomeArmorModel
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.EquipmentSlot

@JvmRecord
data class GnomeArmorSet<T>(val head: T, val chest: T, val legs: T, val feet: T) {
    operator fun get(slot: EquipmentSlot): T = when (slot) {
        EquipmentSlot.HEAD  -> head
        EquipmentSlot.CHEST -> chest
        EquipmentSlot.LEGS  -> legs
        EquipmentSlot.FEET  -> feet
        else -> throw IllegalArgumentException("Unsupported armor slot for Gnome: $slot")
    }

    inline fun <R> map(transform: (T) -> R): GnomeArmorSet<R> =
        GnomeArmorSet(transform(head), transform(chest), transform(legs), transform(feet))

    companion object {
        /**
         * Bakes a set of ModelLayerLocations into actual GnomeModel instances.
         *
         * @param locations The unbaked layer locations for each piece
         * @param modelSet The EntityModelSet from the render context
         * @param factory Function that creates a GnomeModel from a baked ModelPart
         */
        fun bake(
            locations: GnomeArmorSet<ModelLayerLocation>, modelSet: EntityModelSet, factory: (ModelPart) -> GnomeArmorModel<GnomeRenderState>
        ): GnomeArmorSet<GnomeArmorModel<GnomeRenderState>> = locations.map { loc ->
            factory(modelSet.bakeLayer(loc))
        }

        fun bakeDefault(
            locations: GnomeArmorSet<ModelLayerLocation>, modelSet: EntityModelSet
        ): GnomeArmorSet<GnomeArmorModel<GnomeRenderState>> = bake(
            locations, modelSet, ::GnomeArmorModel
        )
    }
}