package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
data class Arbeidsforhold( // TODO: Valider, begrens og escape String beskrivelse
    val inkludertISykefravaer: Boolean, // Inkludert i beregning / sykepengegrunnlag
    val yrkesbeskrivelse: String,
    val stillingsprosent: Double,
    val inntekt: Double,
)
