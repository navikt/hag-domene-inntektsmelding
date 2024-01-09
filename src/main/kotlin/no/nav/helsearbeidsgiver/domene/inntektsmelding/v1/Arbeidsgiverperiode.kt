package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

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
    val begrunnelse: BegrunnelseRedusertLoennIAgp,
)

/** Bruker UpperCamelCase for å matche kodeverkverdier. */
@Serializable
enum class BegrunnelseRedusertLoennIAgp {
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
