@file:UseSerializers(UuidSerializer::class, OffsetDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Kanal
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema.SkjemaInntektsmelding
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class Innsending(
    val innsendingId: UUID,
    val skjema: SkjemaInntektsmelding,
    val aarsakInnsending: AarsakInnsending,
    val type: Inntektsmelding.Type,
    val avsenderSystem: AvsenderSystem,
    val innsendtTid: OffsetDateTime,
    val kanal: Kanal = Kanal.NAV_NO,
    val versjon: Int = 1,
)
