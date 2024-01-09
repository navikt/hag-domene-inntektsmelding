@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonClassDiscriminator
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Deprecated("Bruk 'v1.InntektEndringAarsak' istedenfor.")
@Serializable
@OptIn(ExperimentalSerializationApi::class)
// TODO bytt ved mulighet
@JsonClassDiscriminator("typpe")
sealed class InntektEndringAarsak

@Deprecated("Bruk 'v1.Bonus' istedenfor.")
@Serializable
@SerialName("Bonus")
data class Bonus(
    val aarligBonus: Double? = null,
    val datoForBonus: LocalDate? = null,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Feilregistrert' istedenfor.")
@Serializable
@SerialName("Feilregistrert")
data object Feilregistrert : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Ferie' istedenfor.")
@Serializable
@SerialName("Ferie")
data class Ferie(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Ferietrekk' istedenfor.")
@Serializable
@SerialName("Ferietrekk")
data object Ferietrekk : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Nyansatt' istedenfor.")
@Serializable
@SerialName("Nyansatt")
data object Nyansatt : InntektEndringAarsak()

@Deprecated("Bruk 'v1.NyStilling' istedenfor.")
@Serializable
@SerialName("NyStilling")
data class NyStilling(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.NyStillingsprosent' istedenfor.")
@Serializable
@SerialName("NyStillingsprosent")
data class NyStillingsprosent(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Permisjon' istedenfor.")
@Serializable
@SerialName("Permisjon")
data class Permisjon(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Permittering' istedenfor.")
@Serializable
@SerialName("Permittering")
data class Permittering(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Sykefravaer' istedenfor.")
@Serializable
@SerialName("Sykefravaer")
data class Sykefravaer(
    val liste: List<Periode>,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.Tariffendring' istedenfor.")
@Serializable
@SerialName("Tariffendring")
data class Tariffendring(
    val gjelderFra: LocalDate,
    val bleKjent: LocalDate,
) : InntektEndringAarsak()

@Deprecated("Bruk 'v1.VarigLoennsendring' istedenfor.")
@Serializable
@SerialName("VarigLonnsendring")
data class VarigLonnsendring(
    val gjelderFra: LocalDate,
) : InntektEndringAarsak()
