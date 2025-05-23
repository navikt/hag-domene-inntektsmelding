@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Refusjon(
    val beloepPerMaaned: Double,
    val endringer: List<RefusjonEndring>,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            valider(
                vilkaar = beloepPerMaaned.erStoerreEllerLikNullOgMindreEnnMaks(),
                feilmelding = Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
            ),
            valider(
                vilkaar = endringer.all(RefusjonEndring::erGyldig),
                feilmelding = Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
            ),
        )
}

@Serializable
data class RefusjonEndring(
    val beloep: Double,
    val startdato: LocalDate,
) {
    internal fun erGyldig(): Boolean = beloep.erStoerreEllerLikNullOgMindreEnnMaks()
}
