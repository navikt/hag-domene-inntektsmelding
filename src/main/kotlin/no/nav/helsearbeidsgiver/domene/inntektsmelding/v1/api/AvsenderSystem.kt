@file:UseSerializers(
    UuidSerializer::class,
)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

private val NAV_ORGNR = Orgnr("889640782")
private val NAV_SYSTEMNAVN = "NAV_PORTAL"
private val NAV_SYSTEMVERSJON = "1.0"

@Serializable
data class AvsenderSystem(
    // val systemBrukerId: UUID = UUID.randomUUID(), // TODO: Kan egentlig termineres i LPS-API..?
    val orgnr: Orgnr = NAV_ORGNR, // Teknisk avsender: LPS sitt Orgnr, Nav hvis innsendt fra nav.no
    val avsenderSystemNavn: String = NAV_SYSTEMNAVN,
    val avsenderSystemVersjon: String = NAV_SYSTEMVERSJON,
)
