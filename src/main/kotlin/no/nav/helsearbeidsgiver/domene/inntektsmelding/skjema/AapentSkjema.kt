package no.nav.helsearbeidsgiver.domene.inntektsmelding.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Refusjon

@Serializable
data class AapentSkjema(
    val sykmeldtFnr: String,
    val innsender: SkjemaInnsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt,
    val refusjon: Refusjon,
)
