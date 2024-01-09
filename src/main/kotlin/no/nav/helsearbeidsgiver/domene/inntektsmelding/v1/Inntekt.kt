@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Inntekt(
    val beloep: Double,
    val inntektsdato: LocalDate,
    val naturalytelser: List<Naturalytelse>,
    val endringAarsak: InntektEndringAarsak? = null,
)
