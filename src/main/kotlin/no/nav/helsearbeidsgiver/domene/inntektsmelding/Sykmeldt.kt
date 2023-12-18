package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class Sykmeldt(
    val fnr: String,
    val navn: String,
)
