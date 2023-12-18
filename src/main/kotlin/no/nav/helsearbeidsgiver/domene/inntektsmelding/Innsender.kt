package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class Innsender(
    val orgnr: String,
    val orgNavn: String,
    val fnr: String,
    val navn: String,
    val tlf: String,
)
