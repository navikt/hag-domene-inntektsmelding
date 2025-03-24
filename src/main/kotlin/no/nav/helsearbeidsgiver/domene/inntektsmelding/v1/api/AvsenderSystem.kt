@file:UseSerializers(
    UuidSerializer::class,
)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

val NAV_ORGNR = Orgnr("889640782")
val NAV_SYSTEMNAVN = "NAV_PORTAL"
val NAV_SYSTEMVERSJON = "1.0"

@Serializable
data class AvsenderSystem(
    val orgnr: Orgnr = NAV_ORGNR, // Teknisk avsender: LPS sitt Orgnr, Nav hvis innsendt fra nav.no
    val avsenderSystemNavn: String = NAV_SYSTEMNAVN,
    val avsenderSystemVersjon: String = NAV_SYSTEMVERSJON,
)
