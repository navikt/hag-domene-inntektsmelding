package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.Serializable
import kotlin.String

@Deprecated("Bruk 'v1.AarsakInnsending' istedenfor.")
@Serializable
enum class AarsakInnsending(val value: String) {
    NY("Ny"),
    ENDRING("Endring"),
}
