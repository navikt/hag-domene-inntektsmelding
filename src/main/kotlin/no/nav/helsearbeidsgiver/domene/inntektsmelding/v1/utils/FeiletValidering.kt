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

    // Feil i koden, ingenting bruker kan gjøre
    const val TEKNISK_FEIL = "Det oppsto en feil i systemet. Prøv igjen senere."
}
