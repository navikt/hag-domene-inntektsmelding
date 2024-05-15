package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding.Type as Type

@Deprecated("Bruk 'v1.JournalfoertInntektsmelding' istedenfor.")
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class JournalfoertInntektsmelding(
    val journalpostId: String,
    @JsonNames("inntektsmeldingDokument") // TODO slett etter overgangsfase
    val inntektsmelding: Inntektsmelding,
    val type: Type?, // for å skille på selvbestemt og vanlig i spinosaurus, før V1 tas i bruk overalt
)
