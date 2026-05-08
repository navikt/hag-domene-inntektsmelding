package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import java.math.BigDecimal

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
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = arbeidsforhold.all { gyldigStillingsprosent(it.stillingsprosent) },
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT,
            ),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = arbeidsforhold.all { it.inntekt.erStoerreEllerLikNullOgMindreEnnMaks() },
                feilmelding = Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
            ),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = arbeidsforhold.any { it.inkludertISykefravaer },
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD,
            ),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider(
                vilkaar = !arbeidsforhold.all { it.inkludertISykefravaer },
                feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD,
            ),
        )

    fun sumInntekt(): Double = arbeidsforhold.sumOf { BigDecimal.valueOf(it.inntekt) }.toDouble()

    private fun gyldigStillingsprosent(prosent: Double): Boolean = prosent in 0.0..100.0
}
