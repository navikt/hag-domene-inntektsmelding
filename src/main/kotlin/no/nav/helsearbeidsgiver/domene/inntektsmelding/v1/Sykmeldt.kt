package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
data class Sykmeldt(
    val fnr: String,
    val navn: String,
)
