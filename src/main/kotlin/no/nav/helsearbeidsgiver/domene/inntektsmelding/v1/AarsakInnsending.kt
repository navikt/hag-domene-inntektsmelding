package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
enum class AarsakInnsending {
    Ny,
    Endring,
}
