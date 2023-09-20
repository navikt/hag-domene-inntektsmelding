package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
enum class AarsakInnsending(val value: String) {
    NY("Ny"),
    ENDRING("Endring"),
}
