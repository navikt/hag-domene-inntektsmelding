@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

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
