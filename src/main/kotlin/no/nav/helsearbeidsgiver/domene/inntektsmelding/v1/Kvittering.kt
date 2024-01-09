@file:UseSerializers(OffsetDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class Kvittering(
    val lagretInntektmelding: Inntektsmelding? = null,
    val kvitteringEkstern: KvitteringEkstern? = null,
)

@Serializable
data class KvitteringEkstern(
    val avsenderSystem: String,
    val referanse: String,
    val mottatt: OffsetDateTime,
)
