package redtoss.creativity.cerebro.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Strategy(
    val category: Category,
    val title: String,
    @SerialName("short_description")
    val shortDescription: String,
    @SerialName("long_description")
    val longDescription: String = shortDescription,
)

fun Strategy.serialized(): String = Json.encodeToString(this)
