package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable

@Serializable
data class SkjemaAvsender(
    val orgnr: String,
    val tlf: String,
)
