package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erGyldigTlf
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr

@Serializable
data class SkjemaInntektsmelding(
    val avsenderTlf: String,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt?,
    val refusjon: Refusjon?,
) {
    fun valider(): Set<String> =
        listOfNotNull(
            listOfNotNull(
                valider(
                    vilkaar = avsenderTlf.erGyldigTlf(),
                    feilmelding = Feilmelding.TLF,
                ),
            ),

            agp?.valider(),
            inntekt?.valider(),
            refusjon?.valider(),

            validerRefusjonMotInntekt(refusjon, inntekt),
        )
            .tilFeilmeldinger()
}

@Serializable
data class SkjemaInntektsmeldingSelvbestemt(
    val sykmeldtFnr: Fnr,
    val avsender: SkjemaAvsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt,
    val refusjon: Refusjon?,
) {
    fun valider(): Set<String> =
        listOfNotNull(
            listOfNotNull(
                valider(
                    vilkaar = sykmeldingsperioder.isNotEmpty(),
                    feilmelding = Feilmelding.SYKEMELDINGER_IKKE_TOM,
                ),
            ),

            avsender.valider(),
            agp?.valider(),
            inntekt.valider(),
            refusjon?.valider(),

            validerRefusjonMotInntekt(refusjon, inntekt),
        )
            .tilFeilmeldinger()
}

private fun validerRefusjonMotInntekt(refusjon: Refusjon?, inntekt: Inntekt?): List<FeiletValidering> =
    if (refusjon != null && inntekt != null) {
        listOfNotNull(
            valider(
                vilkaar = refusjon.beloepPerMaaned <= inntekt.beloep,
                feilmelding = Feilmelding.REFUSJON_OVER_INNTEKT,
            ),

            valider(
                vilkaar = refusjon.endringer.all { it.beloep <= inntekt.beloep },
                feilmelding = Feilmelding.REFUSJON_OVER_INNTEKT,
            ),
        )
    } else {
        emptyList()
    }

private fun List<List<FeiletValidering>>.tilFeilmeldinger(): Set<String> =
    flatten()
        .map(FeiletValidering::feilmelding)
        .toSet()
