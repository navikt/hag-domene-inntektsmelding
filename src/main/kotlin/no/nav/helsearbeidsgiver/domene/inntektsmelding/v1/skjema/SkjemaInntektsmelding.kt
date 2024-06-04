package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.bestemmendeFravaersdag
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr

private val sikkerLogger = sikkerLogger()

@Serializable
data class SkjemaInntektsmelding(
    val sykmeldtFnr: String,
    val avsender: SkjemaAvsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt?,
    val refusjon: Refusjon?,
) {
    fun valider(): Set<String> =
        listOfNotNull(
            listOfNotNull(
                valider(
                    vilkaar = Fnr.erGyldig(sykmeldtFnr),
                    feilmelding = Feilmelding.FNR,
                ),
                valider(
                    vilkaar = sykmeldingsperioder.isNotEmpty(),
                    feilmelding = Feilmelding.SYKEMELDINGER_IKKE_TOM,
                ),
            ),

            avsender.valider(),
            agp?.valider(),
            inntekt?.valider(),
            refusjon?.valider(),

            // Må sjekke sykmeldingsperioder fordi beregning av bestemmende fraværsdag krever ikke-tom liste
            if (agp != null && inntekt != null && sykmeldingsperioder.isNotEmpty()) {
                val bestemmendeFravaersdag = bestemmendeFravaersdag(
                    arbeidsgiverperioder = agp.perioder,
                    sykmeldingsperioder = sykmeldingsperioder,
                )

                val feiletValidering = valider(
                    vilkaar = !bestemmendeFravaersdag.isBefore(inntekt.inntektsdato),
                    feilmelding = Feilmelding.TEKNISK_FEIL,
                )

                if (feiletValidering != null) {
                    sikkerLogger.error("Bestemmende fraværsdag er før inntektsdato. Dette er ikke mulig. Bruker hindret fra å sende inn.")
                }

                listOfNotNull(feiletValidering)
            } else {
                emptyList()
            },

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
