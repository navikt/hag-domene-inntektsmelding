package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

internal data class FeiletValidering(
    val feilmelding: String,
)

internal fun valider(vilkaar: Boolean, feilmelding: String): FeiletValidering? =
    if (vilkaar) {
        null
    } else {
        FeiletValidering(feilmelding)
    }

internal object Feilmelding {
    const val ORGNR = "Ugyldig organisasjonsnummer"
    const val FNR = "Ugyldig fødsels- eller D-nummer"
    const val TLF = "Ugyldig telefonnummer"
    const val SYKEMELDINGER_IKKE_TOM = "Sykmeldingsperioder må fylles ut"
    const val KREVER_BELOEP_STOERRE_ELLER_LIK_NULL = "Beløp må være større eller lik 0"
    const val KREVER_BELOEP_STOERRE_ENN_NULL = "Beløp må være større enn 0"
    const val AGP_IKKE_TOM = "Arbeidsgiverperioden må fylles ut, med mindre man betaler redusert lønn i perioden"
    const val AGP_MAKS_16 = "Arbeidsgiverperioden kan være maksimum 16 dager"
    const val REFUSJON_OVER_INNTEKT = "Refusjonsbeløp må være mindre eller lik inntekt"
    const val REFUSJON_ENDRING_DATO = "Refusjonsendringer må være før eller lik siste dato for refusjon"

    // Feil i koden, ingenting bruker kan gjøre
    const val TEKNISK_FEIL = "Det oppsto en feil i systemet. Prøv igjen senere."
}
