package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class Arbeidsgiverperiode(
    val perioder: List<Periode>,
    val egenmeldinger: List<Periode>,
    val redusertLoennIAgp: RedusertLoennIAgp? = null,
)

@Serializable
data class RedusertLoennIAgp(
    val beloep: Double,
    val begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode,
)
