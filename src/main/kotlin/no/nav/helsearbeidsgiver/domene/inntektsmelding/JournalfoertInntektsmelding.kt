package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class JournalfoertInntektsmelding(
    val journalpostId: String,
    @JsonNames("inntektsmeldingDokument") // TODO slett etter overgangsfase
    val inntektsmelding: Inntektsmelding,
)
