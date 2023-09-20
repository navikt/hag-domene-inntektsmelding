@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonClassDiscriminator
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
// TODO bytt ved mulighet
@JsonClassDiscriminator("typpe")
sealed class InntektEndringAarsak

@Serializable
@SerialName("Nyansatt")
data object Nyansatt : InntektEndringAarsak()

@Serializable
@SerialName("Tariffendring")
data class Tariffendring(
    val gjelderFra: LocalDate,
    val bleKjent: LocalDate,
) : InntektEndringAarsak()

@Serializable
@SerialName("Sykefravaer")
data class Sykefravaer(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Ferie")
data class Ferie(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("VarigLonnsendring")
data class VarigLonnsendring(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()

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
@SerialName("Bonus")
data class Bonus(
    val aarligBonus: Double? = null,
    val datoForBonus: LocalDate? = null,
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
@SerialName("Feilregistrert")
data object Feilregistrert : InntektEndringAarsak()
