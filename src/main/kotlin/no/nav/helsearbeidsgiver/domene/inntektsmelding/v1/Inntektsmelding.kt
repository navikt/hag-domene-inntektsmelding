@file:UseSerializers(OffsetDateTimeSerializer::class, UuidSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
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
    val refusjon: Refusjon?,
    val aarsakInnsending: AarsakInnsending,
    val mottatt: OffsetDateTime,
) {
    @Serializable
    sealed class Type {
        abstract val id: UUID

        @Serializable
        @SerialName("Forespurt")
        data class Forespurt(override val id: UUID) : Type()

        @Serializable
        @SerialName("Selvbestemt")
        data class Selvbestemt(override val id: UUID) : Type()
    }
}
