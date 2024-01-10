@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonClassDiscriminator
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("aarsak")
sealed class InntektEndringAarsak

@Serializable
@SerialName("Bonus")
data object Bonus : InntektEndringAarsak()

@Serializable
@SerialName("Feilregistrert")
data object Feilregistrert : InntektEndringAarsak()

@Serializable
@SerialName("Ferie")
data class Ferie(
    val perioder: List<Periode>,
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
    val perioder: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Permittering")
data class Permittering(
    val perioder: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Sykefravaer")
data class Sykefravaer(
    val perioder: List<Periode>,
) : InntektEndringAarsak()

@Serializable
@SerialName("Tariffendring")
data class Tariffendring(
    val gjelderFra: LocalDate,
    val bleKjent: LocalDate,
) : InntektEndringAarsak()

@Serializable
@SerialName("VarigLoennsendring")
data class VarigLoennsendring(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()
