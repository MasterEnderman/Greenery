package teksturepako.greenery.common.config.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class PlantData(
        @Transient
        var name: String = "",
        var worldGen: MutableList<String> = ArrayList(),
        var drops: MutableList<String> = ArrayList(),
        var compatibleFluids: MutableList<String> = ArrayList(),
        var hasTintIndex: Boolean = false,
        var isSolid: Boolean = false,
        var isHarmful: Boolean = false
)