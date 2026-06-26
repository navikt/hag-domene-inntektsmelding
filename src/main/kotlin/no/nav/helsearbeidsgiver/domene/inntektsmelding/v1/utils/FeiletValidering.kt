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
    const val AGP_UNDER_16_KREVER_REDUSERT_LOENN_ELLER_BEHANDLINGSDAGER =
        "Arbeidsgiverperioden må være 16 dager, med mindre man betaler redusert lønn i perioden eller det gjelder behandlingsdager"
    const val REFUSJON_IKKE_OVER_INNTEKT = "Refusjonsbeløp må være mindre eller lik inntekt"
    const val REFUSJON_ENDRING_ETTER_AGP_SLUTT = "Startdato for refusjonsendringer må være etter arbeidsgiverperiode"
    const val REFUSJON_ENDRING_ETTER_INNTEKTDATO = "Startdato for refusjonsendringer må være etter inntektdato"
    const val INNTEKT_ENDRINGSAARSAK_IKKE_DUPLIKAT = "Endringsårsaker kan ikke inneholde duplikater"

    const val FLERE_ARBEIDSFORHOLD_ULIK_LOENN = "Ved innsending av flere arbeidsforhold må de ha ulik lønn"
    const val FLERE_ARBEIDSFORHOLD_IKKE_SYK_FRA_ALLE = "Ved innsending av flere arbeidsforhold kan det ikke være sykefravær fra alle"
    const val FLERE_ARBEIDSFORHOLD_MINST_TO = "Flere arbeidsforhold må inneholde minst to arbeidsforhold"
    const val FLERE_ARBEIDSFORHOLD_MINST_ETT_INKLUDERT = "Minst ett arbeidsforhold må være inkludert i sykepengegrunnlaget"
    const val FLERE_ARBEIDSFORHOLD_IKKE_ALLE_INKLUDERT = "Kan ikke inkludere alle arbeidsforhold, bruk vanlig inntektsmelding"
    const val FLERE_ARBEIDSFORHOLD_INNTEKT_SUM_IKKE_AVVIK = "Summen av inntekter fra flere arbeidsforhold må være lik innrapportert inntekt"
    const val ARBEIDSFORHOLD_YRKESBESKRIVELSE = "Yrkesbeskrivelse er ugyldig"
    const val ARBEIDSFORHOLD_STILLINGSPROSENT = "Stillingsprosent må være mellom 0 og 100"

    // Feil i koden, ingenting bruker kan gjøre
    const val TEKNISK_FEIL = "Det oppsto en feil i systemet. Prøv igjen senere."
}
