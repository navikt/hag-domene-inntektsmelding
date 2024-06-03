package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider

@Serializable
data class Arbeidsgiverperiode(
    val perioder: List<Periode>,
    val egenmeldinger: List<Periode>,
    val redusertLoennIAgp: RedusertLoennIAgp?,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            // AGP kan ikke være tom, unntatt når arbeidsgiver betaler redusert lønn i AGP
            valider(
                vilkaar = perioder.isNotEmpty() || redusertLoennIAgp != null,
                feilmelding = Feilmelding.AGP_IKKE_TOM,
            ),

            redusertLoennIAgp?.valider(),
        )
}

@Serializable
data class RedusertLoennIAgp(
    val beloep: Double,
    val begrunnelse: Begrunnelse,
) {
    /** Bruker UpperCamelCase for å matche kodeverkverdier. */
    @Serializable
    enum class Begrunnelse {
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

    internal fun valider(): FeiletValidering? =
        valider(
            vilkaar = beloep.erStoerreEllerLikNullOgMindreEnnMaks(),
            feilmelding = Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
        )
}
