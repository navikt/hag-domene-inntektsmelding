package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

@Serializable
data class Avsender(
    val orgnr: Orgnr,
    val orgNavn: String,
    val navn: String,
    val tlf: String,
)
