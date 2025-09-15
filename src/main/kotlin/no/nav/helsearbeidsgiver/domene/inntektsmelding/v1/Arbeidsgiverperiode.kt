package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.antallDager
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.tilDatoer
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import java.time.LocalDate
import java.time.temporal.IsoFields

internal const val AGP_MAKS_DAGER = 16
internal const val ANTALL_BEHANDLINGSDAGER = 12

@Serializable
data class Arbeidsgiverperiode(
    val perioder: List<Periode>,
    // TODO vurder å fjerne om man heller kan utlede fra AGP og sykmeldingsperioder
    val egenmeldinger: List<Periode>,
    val redusertLoennIAgp: RedusertLoennIAgp?,
) {
    internal fun valider(): List<FeiletValidering> {
        val perioderAntallDager = perioder.sumOf { it.antallDager() }

        val perioderValidering =
            when {
                perioderAntallDager == AGP_MAKS_DAGER -> null

                perioderAntallDager > AGP_MAKS_DAGER ->
                    valider(
                        vilkaar = false,
                        feilmelding = Feilmelding.AGP_MAKS_16,
                    )

                else ->
                    valider(
                        vilkaar = redusertLoennIAgp != null || erBehandlingsdager(),
                        feilmelding = Feilmelding.AGP_UNDER_16_UTEN_REDUSERT_LOENN_ELLER_BEHANDLINGSDAGER,
                    )
            }

        return listOfNotNull(
            perioderValidering,
            redusertLoennIAgp?.valider(),
        )
    }
}

internal fun Arbeidsgiverperiode.erBehandlingsdager(): Boolean {
    val harUnikeUker =
        perioder
            .tilDatoer()
            .map { it.tilUkeAarPair() }
            .toSet()
            .size == ANTALL_BEHANDLINGSDAGER

    // håndhever ikke at ukene er kant i kant
    return harUnikeUker && perioder.tilDatoer().size == ANTALL_BEHANDLINGSDAGER
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
