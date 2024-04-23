package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr

@Serializable
data class SkjemaInntektsmelding(
    val sykmeldtFnr: String,
    val avsender: SkjemaAvsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt?,
    val refusjon: Refusjon?,
    val aarsakInnsending: AarsakInnsending,
) {
    fun valider(): Set<String> =
        listOfNotNull(
            listOfNotNull(
                valider(
                    vilkaar = Fnr.erGyldig(sykmeldtFnr),
                    feilmelding = Feilmelding.FNR,
                ),
            ),

            avsender.valider(),

            sykmeldingsperioder.let {
                listOfNotNull(
                    valider(
                        vilkaar = it.isNotEmpty(),
                        feilmelding = Feilmelding.SYKEMELDINGER_IKKE_TOM,
                    ),

                    valider(
                        vilkaar = it.all(Periode::erGyldig),
                        feilmelding = Feilmelding.PERIODE,
                    ),
                )
            },

            agp?.valider(),

            inntekt?.valider(),

            refusjon?.valider(),

            if (inntekt != null && refusjon != null) {
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
            },
        )
            .flatten()
            .map(FeiletValidering::feilmelding)
            .toSet()
}
