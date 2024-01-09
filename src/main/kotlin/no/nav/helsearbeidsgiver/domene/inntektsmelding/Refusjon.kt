@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Deprecated("Bruk 'v1.Refusjon' istedenfor.")
@Serializable
data class Refusjon(
    val utbetalerHeleEllerDeler: Boolean,
    val refusjonPrMnd: Double? = null,
    val refusjonOpphører: LocalDate? = null,
    val refusjonEndringer: List<RefusjonEndring>? = null,
)

@Deprecated("Bruk 'v1.RefusjonEndring' istedenfor.")
@Serializable
data class RefusjonEndring(
    val beløp: Double? = null,
    val dato: LocalDate? = null,
)
