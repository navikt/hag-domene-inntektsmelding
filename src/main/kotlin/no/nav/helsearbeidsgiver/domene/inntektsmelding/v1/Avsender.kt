package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
data class Avsender(
    val orgnr: String,
    val orgNavn: String,
    val fnr: String,
    val navn: String,
    val tlf: String,
)
