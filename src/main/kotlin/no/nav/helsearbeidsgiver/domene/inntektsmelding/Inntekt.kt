package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class Inntekt(
    val bekreftet: Boolean,
    val beregnetInntekt: Double,
    @Serializable(with = InntektEndringAarsakTransformer::class)
    val endringÅrsak: InntektEndringAarsak? = null,
    val manueltKorrigert: Boolean,
)
