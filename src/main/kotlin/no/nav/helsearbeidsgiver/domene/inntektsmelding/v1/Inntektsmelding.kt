@file:UseSerializers(OffsetDateTimeSerializer::class, UuidSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api.AvsenderSystem
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.time.OffsetDateTime
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Inntektsmelding(
    val id: UUID,
    val type: Type,
    val sykmeldt: Sykmeldt,
    val avsender: Avsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt?,
    val refusjon: Refusjon?,
    val aarsakInnsending: AarsakInnsending,
    val mottatt: OffsetDateTime,
    val vedtaksperiodeId: UUID? = null,
    // TODO: vedtaksperiodeID skal ikke være nullable
    // - men må vente til alle gamle saker / selvbestemtIMer har blitt slettet - ETA November 2025
) {
    @Serializable
    sealed class Type {
        abstract val id: UUID

        abstract val avsenderSystem: AvsenderSystem

        fun kanal(): Kanal =
            when (this) {
                is ForespurtEkstern -> Kanal.HR_SYSTEM_API
                is Forespurt, is Selvbestemt -> Kanal.NAV_NO
            }

        @Serializable
        @SerialName("Forespurt")
        data class Forespurt(
            override val id: UUID,
        ) : Type() {
            @EncodeDefault
            override val avsenderSystem: AvsenderSystem = AvsenderSystem()
        }

        @Serializable
        @SerialName("Selvbestemt")
        data class Selvbestemt(
            override val id: UUID,
        ) : Type() {
            @EncodeDefault
            override val avsenderSystem: AvsenderSystem = AvsenderSystem()
        }

        @Serializable
        @SerialName("ForespurtEkstern")
        data class ForespurtEkstern(
            override val id: UUID,
            override val avsenderSystem: AvsenderSystem,
        ) : Type()
    }
}

@Serializable
enum class Kanal {
    NAV_NO,
    HR_SYSTEM_API,
    ALTINN,
}
