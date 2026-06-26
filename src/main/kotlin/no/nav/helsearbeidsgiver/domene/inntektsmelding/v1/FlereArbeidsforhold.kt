package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema.ArbeidsforholdType
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import java.math.BigDecimal
import kotlin.collections.sumOf

@Serializable
data class FlereArbeidsforhold(
    val harLikLoenn: Boolean,
    val erSykmeldtFraAlle: Boolean,
    val arbeidsforhold: List<Arbeidsforhold>,
) {
    internal fun valider(): List<FeiletValidering> {
        val overordnedeFeil =
            listOfNotNull(
                valider(
                    vilkaar = !harLikLoenn,
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_ULIK_LOENN,
                ),
                valider(
                    vilkaar = !erSykmeldtFraAlle,
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_IKKE_SYK_FRA_ALLE,
                ),
            )

        val feilPaaTversAvArbeidsforhold =
            listOfNotNull(
                valider(
                    vilkaar = arbeidsforhold.size >= 2,
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_MINST_TO,
                ),
                valider(
                    vilkaar = arbeidsforhold.any { it.inkludertISykefravaer },
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_MINST_ETT_INKLUDERT,
                ),
                valider(
                    vilkaar = !arbeidsforhold.all { it.inkludertISykefravaer },
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_IKKE_ALLE_INKLUDERT,
                ),
            )

        val feilIEnkelteArbeidsforhold = arbeidsforhold.flatMap { it.valider() }

        return overordnedeFeil + feilPaaTversAvArbeidsforhold + feilIEnkelteArbeidsforhold
    }

    internal fun validerMot(inntekt: Inntekt?): List<FeiletValidering> =
        listOfNotNull(
            if (inntekt == null) {
                FeiletValidering(Feilmelding.TEKNISK_FEIL)
            } else {
                valider(
                    vilkaar = sumInntekt() == inntekt.beloep,
                    feilmelding = Feilmelding.FLERE_ARBEIDSFORHOLD_INNTEKT_SUM_IKKE_AVVIK,
                )
            },
        )

    internal fun validerMot(arbeidsforholdType: ArbeidsforholdType): List<FeiletValidering> =
        listOfNotNull(
            when (arbeidsforholdType) {
                is ArbeidsforholdType.MedArbeidsforhold -> null

                ArbeidsforholdType.Behandlingsdager,
                ArbeidsforholdType.Fisker,
                ArbeidsforholdType.UtenArbeidsforhold,
                -> FeiletValidering(Feilmelding.TEKNISK_FEIL)
            },
        )

    private fun sumInntekt(): Double = arbeidsforhold.sumOf { BigDecimal.valueOf(it.inntekt) }.toDouble()
}
