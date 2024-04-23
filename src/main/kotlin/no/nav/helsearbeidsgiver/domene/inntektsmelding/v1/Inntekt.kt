@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erNullEllerOverNullOgUnderMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Inntekt(
    val beloep: Double,
    val inntektsdato: LocalDate,
    val naturalytelser: List<Naturalytelse>,
    val endringAarsak: InntektEndringAarsak?,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            valider(
                vilkaar = beloep.erNullEllerOverNullOgUnderMaks(),
                feilmelding = Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL,
            ),

            valider(
                vilkaar = naturalytelser.all(Naturalytelse::erGyldig),
                feilmelding = Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL,
            ),
        )
}
