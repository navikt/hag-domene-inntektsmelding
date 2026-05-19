package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erGyldigEllerBlankString
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider

@Serializable
data class Arbeidsforhold(
    val inkludertISykefravaer: Boolean, // Inkludert i beregning / sykepengegrunnlag
    val yrkesbeskrivelse: String,
    val stillingsprosent: Double,
    val inntekt: Double,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            valider(
                vilkaar = yrkesbeskrivelse.erGyldigEllerBlankString(),
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_YRKESBESKRIVELSE,
            ),
            valider(
                vilkaar = stillingsprosent in 0.0..100.0,
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT,
            ),
            valider(
                vilkaar = inntekt.erStoerreEllerLikNullOgMindreEnnMaks(),
                feilmelding = Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
            ),
        )
}
