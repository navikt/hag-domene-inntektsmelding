package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

/** @param orgnr Teknisk avsender: LPS sitt orgnr, eller Navs orgnr hvis innsendt via nav.no */
@Serializable
data class AvsenderSystem(
    val orgnr: Orgnr,
    val navn: String,
    val versjon: String,
) {
    companion object {
        val nav =
            AvsenderSystem(
                orgnr = Orgnr("889640782"),
                navn = "NAV_PORTAL",
                versjon = "1.0",
            )
    }
}
