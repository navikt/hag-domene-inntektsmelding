package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding

@Serializable
data class FlereArbeidsforhold(
    val harLikLoenn: Boolean,
    val erSykmeldtFraAlle: Boolean,
    val arbeidsforhold: List<Arbeidsforhold>,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = !harLikLoenn,
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_MED_LIK_LOENN,
            ),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = !erSykmeldtFraAlle,
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_SYK_FRA_ALLE,
            ),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = arbeidsforhold.size > 1,
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_MAA_HA_MINST_TO,
            ),
        )
}
