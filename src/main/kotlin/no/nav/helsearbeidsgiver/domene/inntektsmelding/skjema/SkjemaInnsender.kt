package no.nav.helsearbeidsgiver.domene.inntektsmelding.skjema

import kotlinx.serialization.Serializable

@Serializable
data class SkjemaInnsender(
    val orgnr: String,
    val tlf: String,
)
