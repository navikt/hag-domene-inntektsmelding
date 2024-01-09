package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Deprecated("Bruk 'v1.Inntekt' istedenfor.")
@Serializable
data class Inntekt(
    val bekreftet: Boolean,
    val beregnetInntekt: Double,
    val endringÅrsak: InntektEndringAarsak? = null,
    val manueltKorrigert: Boolean,
)
