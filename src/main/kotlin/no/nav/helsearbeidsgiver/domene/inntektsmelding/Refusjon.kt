@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate
import kotlin.Boolean
import kotlin.collections.List

@Serializable
data class Refusjon(
    val beloepPerMaaned: Double,
    val endringer: List<RefusjonEndring>,
    val sluttdato: LocalDate? = null,
)

@Serializable
data class RefusjonEndring(
    val beloep: Double,
    val startdato: LocalDate,
)

@Deprecated("Bruk 'Refusjon' istedenfor.")
@Serializable
data class RefusjonDeprecated(
    val utbetalerHeleEllerDeler: Boolean,
    val refusjonPrMnd: Double? = null,
    val refusjonOpphører: LocalDate? = null,
    val refusjonEndringer: List<RefusjonEndringDeprecated>? = null,
)

@Deprecated("Bruk 'RefusjonEndring' istedenfor.")
@Serializable
data class RefusjonEndringDeprecated(
    val beløp: Double? = null,
    val dato: LocalDate? = null,
)
