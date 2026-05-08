package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

internal data class FeiletValidering(
    val feilmelding: String,
)

internal fun valider(
    vilkaar: Boolean,
    feilmelding: String,
): FeiletValidering? =
    if (vilkaar) {
        null
    } else {
        FeiletValidering(feilmelding)
    }

internal object Feilmelding {
    const val TLF = "Ugyldig telefonnummer"
    const val SYKEMELDINGER_IKKE_TOM = "Sykmeldingsperioder må fylles ut"
    const val KREVER_BELOEP_STOERRE_ELLER_LIK_NULL = "Beløp må være større eller lik 0"
    const val KREVER_BELOEP_STOERRE_ENN_NULL = "Beløp må være større enn 0"
    const val AGP_MAKS_16 = "Arbeidsgiverperioden kan være maksimum 16 dager"
    const val AGP_UNDER_16_UTEN_REDUSERT_LOENN_ELLER_BEHANDLINGSDAGER =
        "Arbeidsgiverperioden må være 16 dager, med mindre man betaler redusert lønn i perioden eller det gjelder behandlingsdager"
    const val REFUSJON_OVER_INNTEKT = "Refusjonsbeløp må være mindre eller lik inntekt"
    const val REFUSJON_ENDRING_FOER_AGP_SLUTT = "Startdato for refusjonsendringer må være etter arbeidsgiverperiode"
    const val REFUSJON_ENDRING_FOER_INNTEKTDATO = "Startdato for refusjonsendringer må være etter inntektdato"
    const val DUPLIKAT_INNTEKT_ENDRINGSAARSAK = "Endringsårsaker kan ikke inneholde duplikater"

    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_MAA_HA_MINST_TO = "Flere arbeidsforhold må inneholde minst to arbeidsforhold"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD = "Minst ett arbeidsforhold skal være inkludert i sykepengegrunnlag"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD = "Kan ikke inkludere alle arbeidsforhold, bruk vanlig inntektsmelding"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_MED_LIK_LOENN = "Ved innsending av flere arbeidsforhold må de ha ulik lønn"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_SYK_FRA_ALLE = "Ved innsending av flere arbeidsforhold kan det ikke være sykefravær fra alle"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT = "Faktisk stillingsprosent må være mellom 0 og 100"
    const val UGYLDIG_FLERE_ARBEIDSFORHOLD_INNTEKT_AVVIK = "Summen av inntekter fra flere arbeidsforhold må være lik innrapportert inntekt"

    // Feil i koden, ingenting bruker kan gjøre
    const val TEKNISK_FEIL = "Det oppsto en feil i systemet. Prøv igjen senere."
}
