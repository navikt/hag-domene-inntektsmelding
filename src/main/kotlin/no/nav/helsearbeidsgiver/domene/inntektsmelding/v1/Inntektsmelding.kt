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

@Serializable
data class Inntektsmelding(
    val id: UUID,
    val type: Type,
    val sykmeldt: Sykmeldt,
    val avsender: Avsender,
    val sykmeldingsperioder: List<Periode>,
    val agp: Arbeidsgiverperiode?,
    val inntekt: Inntekt?,
    val naturalytelser: List<Naturalytelse>,
    val refusjon: Refusjon?,
    val aarsakInnsending: AarsakInnsending,
    val mottatt: OffsetDateTime,
    val vedtaksperiodeId: UUID? = null, // nullable for å støtte fisker og utenArbeidsforhold
) {
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    sealed class Type {
        abstract val id: UUID

        open val avsenderSystem: AvsenderSystem
            get() = AvsenderSystem.nav

        val kanal: Kanal
            get() =
                when (this) {
                    is ForespurtEkstern -> Kanal.HR_SYSTEM_API
                    is Forespurt, is Selvbestemt, is Fisker, is UtenArbeidsforhold, is Behandlingsdager -> Kanal.NAV_NO
                }

        @Serializable
        @SerialName("Forespurt")
        data class Forespurt(
            override val id: UUID,
            @EncodeDefault
            val erAgpForespurt: Boolean = true,
        ) : Type()

        @Serializable
        @SerialName("ForespurtEkstern")
        data class ForespurtEkstern(
            override val id: UUID,
            @EncodeDefault
            val erAgpForespurt: Boolean = true,
            private val _avsenderSystem: AvsenderSystem,
        ) : Type() {
            override val avsenderSystem: AvsenderSystem
                get() = _avsenderSystem
        }

        @Serializable
        @SerialName("Selvbestemt")
        data class Selvbestemt(
            override val id: UUID,
        ) : Type()

        @Serializable
        @SerialName("Fisker")
        data class Fisker(
            override val id: UUID,
        ) : Type()

        @Serializable
        @SerialName("UtenArbeidsforhold")
        data class UtenArbeidsforhold(
            override val id: UUID,
        ) : Type()

        @Serializable
        @SerialName("Behandlingsdager")
        data class Behandlingsdager(
            override val id: UUID,
        ) : Type()
    }
}

@Serializable
enum class Kanal {
    NAV_NO,
    HR_SYSTEM_API,
}
