@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
)

infix fun LocalDate.til(tom: LocalDate): Periode =
    Periode(
        fom = this,
        tom = tom,
    )
