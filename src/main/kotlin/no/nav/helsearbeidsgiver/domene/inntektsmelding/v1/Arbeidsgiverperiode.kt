package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.agpPaavirkerIkkeInntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.antallDager
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erStoerreEllerLikNullOgMindreEnnMaks
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.slaaSammenSammenhengendePerioder
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.IsoFields

internal const val AGP_MAKS_DAGER = 16
internal const val ANTALL_BEHANDLINGSDAGER = 12

@Serializable
data class Arbeidsgiverperiode(
    val perioder: List<Periode>,
    val redusertLoennIAgp: RedusertLoennIAgp?,
) {
    fun validerMotSykmeldingsperioder(
        erAgpForespurt: Boolean,
        egenmeldingerFraForespoersel: List<Periode>,
        sykmeldingsperioder: List<Periode>,
    ): Set<String> =
        validerMotSykmeldingsperioderIntern(
            erAgpForespurt = erAgpForespurt,
            egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
            sykmeldingsperioder = sykmeldingsperioder,
        ).map(FeiletValidering::feilmelding)
            .toSet()

    fun utledEgenmeldinger(sykmeldingsperioder: List<Periode>): List<Periode> {
        val agpStart = perioder.minOfOrNull { it.fom }
        val agpSlutt = perioder.maxOfOrNull { it.tom }
        val sykmeldingerStart = sykmeldingsperioder.minOf { it.fom }

        return if (
            agpStart == null ||
            agpSlutt == null ||
            agpPaavirkerIkkeInntektsmelding(agpSlutt, sykmeldingerStart)
        ) {
            emptyList()
        } else {
            val agpDager = perioder.tilDager()
            val loerdagerEtterSykFredag =
                agpDager
                    .filter { it.dayOfWeek == DayOfWeek.FRIDAY }
                    .map { it.plusDays(1) }
                    .toSet()
            val soendagerEtterSykFredagOgLoerdag =
                agpDager
                    .filter { it.dayOfWeek == DayOfWeek.SATURDAY }
                    .map { it.plusDays(1) }
                    .toSet()
                    .let { soendagerEtterSykLoerdag ->
                        val soendagerEtterSykFredag = loerdagerEtterSykFredag.map { it.plusDays(1) }
                        soendagerEtterSykFredag.intersect(soendagerEtterSykLoerdag)
                    }

            agpDager
                .minus(
                    sykmeldingsperioder.tilDager(),
                ).minus(loerdagerEtterSykFredag)
                .minus(soendagerEtterSykFredagOgLoerdag)
                .tilPerioder()
        }
    }

    /** Etter sykmelding så må arbeid gjenopptas før egenmelding kan benyttes, ref. [rundskriv om §8-24 fjerde ledd](https://lovdata.no/nav/rundskriv/r08-00#KAPITTEL_4-10-4). */
    private fun harGyldigeEgenmeldinger(sykmeldingsperioder: List<Periode>): Boolean {
        val dagenFoerEgenmeldinger =
            utledEgenmeldinger(sykmeldingsperioder)
                .map { it.fom.minusDays(1) }
                .toSet()

        val sykmeldingsdagerIAgp =
            perioder
                .tilDager()
                .intersect(
                    sykmeldingsperioder.tilDager(),
                )

        return dagenFoerEgenmeldinger.intersect(sykmeldingsdagerIAgp).isEmpty()
    }

    /** Ikke-forespurt AGP _må_ indikere lengre gap til forrige sykmelding (arbeid på minst én sykefraværsdag). */
    private fun erGyldigSomIkkeForespurt(
        egenmeldingerFraForespoersel: List<Periode>,
        sykmeldingsperioder: List<Periode>,
    ): Boolean {
        val agpStart = perioder.minOfOrNull { it.fom }
        val sykefravaerStart = (egenmeldingerFraForespoersel + sykmeldingsperioder).minOf { it.fom }
        val sykmeldingerSlutt = sykmeldingsperioder.maxOf { it.tom }

        return agpStart == null ||
            (
                agpStart.isAfter(sykefravaerStart) &&
                    !agpStart.isAfter(sykmeldingerSlutt)
            )
    }

    internal fun valider(): List<FeiletValidering> {
        val perioderAntallDager = perioder.sumOf { it.antallDager() }

        val perioderValidering =
            when {
                perioderAntallDager == AGP_MAKS_DAGER -> {
                    null
                }

                perioderAntallDager > AGP_MAKS_DAGER -> {
                    valider(
                        vilkaar = false,
                        feilmelding = Feilmelding.AGP_MAKS_16,
                    )
                }

                else -> {
                    valider(
                        vilkaar = redusertLoennIAgp != null || erBehandlingsdager(),
                        feilmelding = Feilmelding.AGP_UNDER_16_KREVER_REDUSERT_LOENN_ELLER_BEHANDLINGSDAGER,
                    )
                }
            }

        return listOfNotNull(
            perioderValidering,
            redusertLoennIAgp?.valider(),
        )
    }

    internal fun validerMotSykmeldingsperioderIntern(
        erAgpForespurt: Boolean,
        egenmeldingerFraForespoersel: List<Periode>,
        sykmeldingsperioder: List<Periode>,
    ): List<FeiletValidering> =
        if (sykmeldingsperioder.isEmpty()) {
            // Validering krever ikke-tom sykmeldingsperioder
            emptyList()
        } else {
            listOfNotNull(
                valider(
                    vilkaar = harGyldigeEgenmeldinger(sykmeldingsperioder),
                    feilmelding = Feilmelding.AGP_EGENMELDING_ETTER_GJENOPPTATT_ARBEID,
                ),
                valider(
                    vilkaar =
                        erAgpForespurt ||
                            erGyldigSomIkkeForespurt(egenmeldingerFraForespoersel, sykmeldingsperioder),
                    feilmelding = Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER,
                ),
            )
        }

    private fun erBehandlingsdager(): Boolean {
        val agpDager = perioder.tilDager()
        val harUnikeUker =
            agpDager
                .map { it.tilUkeAarPair() }
                .toSet()
                .size == ANTALL_BEHANDLINGSDAGER

        // håndhever ikke at ukene er kant i kant
        return harUnikeUker && agpDager.size == ANTALL_BEHANDLINGSDAGER
    }
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

private fun List<Periode>.tilDager(): Set<LocalDate> =
    flatMap {
        List(it.antallDager()) { index ->
            it.fom.plusDays(index.toLong())
        }
    }.toSet()

private fun Set<LocalDate>.tilPerioder(): List<Periode> =
    map { Periode(it, it) }
        .slaaSammenSammenhengendePerioder(ignorerHelgegap = false)

private fun LocalDate.tilUkeAarPair(): Pair<Int, Int> = get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) to get(IsoFields.WEEK_BASED_YEAR)
