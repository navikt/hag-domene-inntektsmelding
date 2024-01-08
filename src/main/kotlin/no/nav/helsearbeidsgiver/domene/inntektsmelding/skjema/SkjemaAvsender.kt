package no.nav.helsearbeidsgiver.domene.inntektsmelding.skjema

import kotlinx.serialization.Serializable

@Serializable
data class SkjemaAvsender(
    val orgnr: String,
    val tlf: String,
)
