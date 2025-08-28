package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.daysUntil
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.sumAntallDager
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import java.time.LocalDate
import java.time.temporal.IsoFields

internal const val AGP_MAKS_DAGER = 16

@Serializable
data class Arbeidsgiverperiode(
    val perioder: List<Periode>,
    // TODO vurder å fjerne om man heller kan utlede fra AGP og sykmeldingsperioder
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
            valider(
                vilkaar = perioder.sumAntallDager() <= AGP_MAKS_DAGER,
                feilmelding = Feilmelding.AGP_MAKS_16,
            ),
            valider(
                vilkaar = perioder.sumAntallDager() == 16 || erBehandlingsdager() || redusertLoennIAgp != null || perioder.isEmpty(),
                feilmelding = Feilmelding.AGP_UNDER_16_OG_IKKE_GYLDIGE_BEHANDLINGSDAGER,
            ),
            redusertLoennIAgp?.valider(),
        )
}

internal fun Arbeidsgiverperiode.erBehandlingsdager(): Boolean {
    val perioderErEnkelteDager = !perioder.map { it.fom.daysUntil(it.tom) + 1 }.any { it != 1 }
    val enPeriodePerUke = perioder.map { it.fom.tilUkeAarPair() }.toSet().size == perioder.size

    return perioderErEnkelteDager && enPeriodePerUke && perioder.size == 12
}

internal fun LocalDate.tilUkeAarPair(): Pair<Int, Int> = get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) to get(IsoFields.WEEK_BASED_YEAR)

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
