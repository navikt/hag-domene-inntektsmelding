@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema.ArbeidsforholdType
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.collections.sumOf

@Serializable
data class FlereArbeidsforhold(
    val harLikLoenn: Boolean,
    val erSykmeldtFraAlle: Boolean,
    val arbeidsforholdPerSykmeldingStartdato: Map<LocalDate, List<Arbeidsforhold>>,
) {
    internal fun valider(): List<FeiletValidering> {
        val overordnedeFeil =
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
                    vilkaar = arbeidsforholdPerSykmeldingStartdato.isNotEmpty(),
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_IKKE_TOM,
                ),
            )

        val feilPaaTversAvArbeidsforhold =
            arbeidsforholdPerSykmeldingStartdato.values.flatMap { arbeidsforhold ->
                listOfNotNull(
                    valider(
                        vilkaar = arbeidsforhold.size >= 2,
                        feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_STARTDATO_MINST_TO,
                    ),
                    valider(
                        vilkaar = arbeidsforhold.any { it.inkludertISykefravaer },
                        feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_STARTDATO_INGEN_ARBEIDSFORHOLD,
                    ),
                    valider(
                        vilkaar = !arbeidsforhold.all { it.inkludertISykefravaer },
                        feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_STARTDATO_ALLE_ARBEIDSFORHOLD,
                    ),
                )
            }

        val feilIEnkelteArbeidsforhold = arbeidsforholdPerSykmeldingStartdato.values.flatten().flatMap { it.valider() }

        return overordnedeFeil + feilPaaTversAvArbeidsforhold + feilIEnkelteArbeidsforhold
    }

    internal fun validerMot(inntekt: Inntekt?): List<FeiletValidering> =
        listOfNotNull(
            if (inntekt == null) {
                FeiletValidering(Feilmelding.TEKNISK_FEIL)
            } else {
                valider(
                    vilkaar = sumInntektPerStartdato().all { it == inntekt.beloep },
                    feilmelding = Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_STARTDATO_INNTEKT_AVVIK,
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

    private fun sumInntektPerStartdato(): List<Double> =
        arbeidsforholdPerSykmeldingStartdato.values.map { arbeidsforhold ->
            arbeidsforhold.sumOf { BigDecimal.valueOf(it.inntekt) }.toDouble()
        }
}
