package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr

@Serializable
data class Sykmeldt(
    val fnr: Fnr,
    val navn: String,
)
