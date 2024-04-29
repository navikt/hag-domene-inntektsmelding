package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.agpPaavirkerIkkeSykmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.daysUntil
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.slaaSammenSammenhengendePerioder
import java.time.LocalDate

fun bestemmendeFravaersdag(
    arbeidsgiverperioder: List<Periode>,
    sykmeldingsperioder: List<Periode>,
): LocalDate {
    val agpSlutt = arbeidsgiverperioder.lastOrNull()?.tom
    val sykmeldingsperioderStart = sykmeldingsperioder.first().fom

    val sammenhengendeSykmeldingsperioder = sykmeldingsperioder.slaaSammenSammenhengendePerioder(ignorerHelgegap = true)

    val sammenhengendeFravaersperioder = if (
        agpSlutt == null ||
        agpPaavirkerIkkeSykmelding(agpSlutt, sykmeldingsperioderStart)
    ) {
        sammenhengendeSykmeldingsperioder
    } else {
        val sammenhengendeSykmeldingsperioderUtenAgp = sammenhengendeSykmeldingsperioder.fjernDatoerTilOgMed(agpSlutt)

        (arbeidsgiverperioder + sammenhengendeSykmeldingsperioderUtenAgp)
            .slaaSammenSammenhengendePerioder(ignorerHelgegap = false)
            .fjernPerioderEtterFoersteUtoverAgp()
    }

    return sammenhengendeFravaersperioder.last().fom
}

private fun List<Periode>.fjernDatoerTilOgMed(grenseTom: LocalDate): List<Periode> =
    mapNotNull {
        if (it.tom.isAfter(grenseTom)) {
            it.copy(
                fom = maxOf(
                    it.fom,
                    grenseTom.plusDays(1),
                ),
            )
        } else {
            null
        }
    }

private fun List<Periode>.fjernPerioderEtterFoersteUtoverAgp(): List<Periode> =
    filterIndexed { index, _ ->
        val antallForegaaendeDager = slice(0..<index)
            .sumOf {
                it.fom.daysUntil(it.tom) + 1
            }

        antallForegaaendeDager <= 16
    }
