package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable

@Serializable
data class ArbeidsforholdDetaljer( // TODO: Valider prosent og inntekt
    val arbeidsforholdsId: String,
    val inkludertISykefravaer: Boolean, // Inkludert i beregning / sykepengegrunnlag
    val stillingsprosent: Double,
    val inntekt: Double,
)
