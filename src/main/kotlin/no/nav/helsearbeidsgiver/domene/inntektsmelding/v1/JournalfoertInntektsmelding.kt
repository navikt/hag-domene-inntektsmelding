package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
data class JournalfoertInntektsmelding(
    val journalpostId: String,
    val inntektsmelding: Inntektsmelding,
)
