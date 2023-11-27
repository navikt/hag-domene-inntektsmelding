@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("aarsak")
sealed class InntektEndringAarsak

@Serializer(forClass = InntektEndringAarsak::class)
object InntektEndringAarsakTransformer : JsonTransformingSerializer<InntektEndringAarsak>(InntektEndringAarsak.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonObject && element.containsKey("typpe")) {
            val mutableMap = element.toMutableMap()
            mutableMap["aarsak"] = mutableMap.remove("typpe")!!
            return JsonObject(mutableMap)
        }
        return element
    }
}

@Serializable
@SerialName("Bonus")
data class Bonus(
    val aarligBonus: Double? = null,
    val datoForBonus: LocalDate? = null,
) : InntektEndringAarsak()

@Serializable
@SerialName("Feilregistrert")
data object Feilregistrert : InntektEndringAarsak()

@Serializable
@SerialName("Ferie")
data class Ferie(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Ferietrekk")
data object Ferietrekk : InntektEndringAarsak()

@Serializable
@SerialName("Nyansatt")
data object Nyansatt : InntektEndringAarsak()

@Serializable
@SerialName("NyStilling")
data class NyStilling(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()

@Serializable
@SerialName("NyStillingsprosent")
data class NyStillingsprosent(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()

@Serializable
@SerialName("Permisjon")
data class Permisjon(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Permittering")
data class Permittering(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Sykefravaer")
data class Sykefravaer(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Tariffendring")
data class Tariffendring(
    val gjelderFra: LocalDate,
    val bleKjent: LocalDate,
) : InntektEndringAarsak()

@Serializable
@SerialName("VarigLonnsendring")
data class VarigLonnsendring(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()
