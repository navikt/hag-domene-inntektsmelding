package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class FullLoennIArbeidsgiverPerioden(
    val utbetalerFullLønn: Boolean,
    val begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode? = null,
    val utbetalt: Double? = null,
)
