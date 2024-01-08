package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
enum class AarsakInnsending(val value: String) {
    NY("Ny"),
    ENDRING("Endring"),
}
