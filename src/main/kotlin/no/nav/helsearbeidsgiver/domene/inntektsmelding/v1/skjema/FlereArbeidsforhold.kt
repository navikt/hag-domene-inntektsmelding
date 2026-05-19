package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.TEKNISK_FEIL
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import java.math.BigDecimal

@Serializable
data class FlereArbeidsforhold(
    val harLikLoenn: Boolean,
    val erSykmeldtFraAlle: Boolean,
    val arbeidsforhold: List<Arbeidsforhold>,
) {
    internal fun valider(): List<FeiletValidering> =
        arbeidsforhold.flatMap { it.valider() } +
            listOfNotNull(
                valider(
                    vilkaar = !harLikLoenn,
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_MED_LIK_LOENN,
                ),
                valider(
                    vilkaar = !erSykmeldtFraAlle,
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_SYK_FRA_ALLE,
                ),
                valider(
                    vilkaar = arbeidsforhold.size > 1,
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_MAA_HA_MINST_TO,
                ),
                valider(
                    vilkaar = arbeidsforhold.any { it.inkludertISykefravaer },
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD,
                ),
                valider(
                    vilkaar = !arbeidsforhold.all { it.inkludertISykefravaer },
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD,
                ),
            )

    internal fun validerMot(inntekt: Inntekt?): List<FeiletValidering> =
        listOfNotNull(
            if (inntekt == null) {
                FeiletValidering(TEKNISK_FEIL)
            } else {
                valider(
                    vilkaar = sumInntekt() == inntekt.beloep,
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_INNTEKT_AVVIK,
                )
            },
        )

    private fun sumInntekt(): Double = arbeidsforhold.sumOf { BigDecimal.valueOf(it.inntekt) }.toDouble()
}
