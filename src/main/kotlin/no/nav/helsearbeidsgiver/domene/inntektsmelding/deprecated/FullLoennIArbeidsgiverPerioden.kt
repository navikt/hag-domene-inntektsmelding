package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.Serializable

@Deprecated("Bruk 'v1.RedusertLoennIAgp' istedenfor.")
@Serializable
data class FullLoennIArbeidsgiverPerioden(
    val utbetalerFullLønn: Boolean,
    val begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode? = null,
    val utbetalt: Double? = null,
)

/** Bruker UpperCamelCase for å matche kodeverkverdier. */
@Deprecated("Bruk 'v1.BegrunnelseRedusertLoennIAgp' istedenfor.")
@Serializable
enum class BegrunnelseIngenEllerRedusertUtbetalingKode {
    ArbeidOpphoert,
    BeskjedGittForSent,
    BetvilerArbeidsufoerhet,
    FerieEllerAvspasering,
    FiskerMedHyre,
    FravaerUtenGyldigGrunn,
    IkkeFravaer,
    IkkeFullStillingsandel,
    IkkeLoenn,
    LovligFravaer,
    ManglerOpptjening,
    Permittering,
    Saerregler,
    StreikEllerLockout,
    TidligereVirksomhet,
}
