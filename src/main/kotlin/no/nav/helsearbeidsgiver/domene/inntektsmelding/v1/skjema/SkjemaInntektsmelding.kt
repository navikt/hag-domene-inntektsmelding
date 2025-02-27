@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.bestemmendeFravaersdag
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erGyldigTlf
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import java.util.UUID

private val sikkerLogger = sikkerLogger()

@Serializable
data class SkjemaInntektsmelding(
    val forespoerselId: UUID,
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
            validerRefusjonMotAgp(refusjon, agp),
        ).tilFeilmeldinger()
}

@Serializable
data class SkjemaInntektsmeldingSelvbestemt(
    val selvbestemtId: UUID?,
    val sykmeldtFnr: Fnr,
    val avsender: SkjemaAvsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt,
    val refusjon: Refusjon?,
    val vedtaksperiodeId: UUID? = null, // TODO: Skal ikke være nullable - Endre når frontend har implementert og er i produksjon
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
            validerBestemmendeFravaersdagMotInntektsdato(agp, inntekt, sykmeldingsperioder),
            validerRefusjonMotInntekt(refusjon, inntekt),
            validerRefusjonMotAgp(refusjon, agp),
        ).tilFeilmeldinger()
}

private fun validerBestemmendeFravaersdagMotInntektsdato(
    agp: Arbeidsgiverperiode?,
    inntekt: Inntekt?,
    sykmeldingsperioder: List<Periode>,
): List<FeiletValidering> =
    // Må sjekke sykmeldingsperioder fordi beregning av bestemmende fraværsdag krever ikke-tom liste
    if (agp != null && inntekt != null && sykmeldingsperioder.isNotEmpty()) {
        val bestemmendeFravaersdag =
            bestemmendeFravaersdag(
                arbeidsgiverperioder = agp.perioder,
                sykefravaersperioder = sykmeldingsperioder,
            )

        val feiletValidering =
            valider(
                vilkaar = !bestemmendeFravaersdag.isBefore(inntekt.inntektsdato),
                feilmelding = Feilmelding.TEKNISK_FEIL,
            )

        if (feiletValidering != null) {
            sikkerLogger.error("Bestemmende fraværsdag er før inntektsdato. Dette er ikke mulig. Bruker hindret fra å sende inn.")
        }

        listOfNotNull(feiletValidering)
    } else {
        emptyList()
    }

private fun validerRefusjonMotInntekt(
    refusjon: Refusjon?,
    inntekt: Inntekt?,
): List<FeiletValidering> =
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
            // "Fallback"-sjekk dersom ingen AGP - da skal dato for refusjonEndring alltid være senere enn InntektDato
            valider(
                vilkaar = refusjon.endringer.all { it.startdato.isAfter(inntekt.inntektsdato) },
                feilmelding = Feilmelding.REFUSJON_ENDRING_FOER_INNTEKTDATO,
            ),

        )
    } else {
        emptyList()
    }

/*
Endring i refusjon skal alltid ha dato etter AGP (dersom det er AGP).
 */
private fun validerRefusjonMotAgp(refusjon: Refusjon?, agp: Arbeidsgiverperiode?): List<FeiletValidering> {
    val agpMax = agp?.perioder?.maxOfOrNull { it.tom }
    if (agpMax == null) {
        return emptyList()
    } else {
        return refusjon?.endringer?.mapNotNull { endring ->
            valider(
                vilkaar = endring.startdato.isAfter(agpMax),
                feilmelding = Feilmelding.REFUSJON_ENDRING_FOER_AGP_SLUTT,
            )
        }.orEmpty()
    }
}

private fun List<List<FeiletValidering>>.tilFeilmeldinger(): Set<String> =
    flatten()
        .map(FeiletValidering::feilmelding)
        .toSet()
