@file:UseSerializers(
    UuidSerializer::class,
)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

val NAV_ORGNR = Orgnr("889640782")
val NAV_SYSTEMNAVN = "NAV_PORTAL"
val NAV_SYSTEMVERSJON = "1.0"

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AvsenderSystem(
    @EncodeDefault
    val orgnr: Orgnr = NAV_ORGNR, // Teknisk avsender: LPS sitt Orgnr, Nav hvis innsendt fra nav.no
    @EncodeDefault
    val navn: String = NAV_SYSTEMNAVN,
    @EncodeDefault
    val versjon: String = NAV_SYSTEMVERSJON,
)
