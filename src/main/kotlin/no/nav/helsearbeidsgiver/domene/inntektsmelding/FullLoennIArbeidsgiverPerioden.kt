package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable

@Serializable
data class FullLoennIArbeidsgiverPerioden(
    val utbetalerFullLønn: Boolean,
    val begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode? = null,
    val utbetalt: Double? = null,
)

/** Bruker UpperCamelCase for å matche kodeverkverdier.  */
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
