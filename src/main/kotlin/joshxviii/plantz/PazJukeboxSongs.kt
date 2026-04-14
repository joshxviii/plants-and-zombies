package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.JukeboxSong

object PazJukeboxSongs {

    @JvmField val GRASSY_GROOVE = registerJukeboxSong("grassy_groove")

    private fun registerJukeboxSong(name: String) : ResourceKey<JukeboxSong> {
        return ResourceKey.create(Registries.JUKEBOX_SONG, pazResource(name) )
    }

    fun initialize() {}
}