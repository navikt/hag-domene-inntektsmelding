package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun LocalDate.daysUntil(other: LocalDate): Int =
    until(other, ChronoUnit.DAYS).toInt()

internal fun agpPaavirkerIkkeSykmelding(agpSlutt: LocalDate, sykmeldingsperioderStart: LocalDate): Boolean =
    agpSlutt.daysUntil(sykmeldingsperioderStart) > 16

internal fun List<Periode>.slaaSammenSammenhengendePerioder(ignorerHelgegap: Boolean): List<Periode> {
    val kanSlaasSammen = if (ignorerHelgegap) {
        ::erSammenhengendeIgnorerHelgegap
    } else {
        ::erSammenhengende
    }

    return sortedBy { it.fom }
        .fold(emptyList()) { slaattSammen, periode ->
            val forrige = slaattSammen.lastOrNull()

            if (forrige != null && kanSlaasSammen(forrige, periode)) {
                val sammenhengende = Periode(
                    fom = forrige.fom,
                    tom = maxOf(forrige.tom, periode.tom),
                )

                slaattSammen.dropLast(1).plus(sammenhengende)
            } else {
                slaattSammen.plus(periode)
            }
        }
}

private fun erSammenhengende(denne: Periode, neste: Periode): Boolean =
    denne.tom.daysUntil(neste.fom) <= 1

private fun erSammenhengendeIgnorerHelgegap(denne: Periode, neste: Periode): Boolean {
    val dagerAvstand = denne.tom.daysUntil(neste.fom)
    return when (denne.tom.dayOfWeek) {
        DayOfWeek.FRIDAY -> dagerAvstand <= 3
        DayOfWeek.SATURDAY -> dagerAvstand <= 2
        else -> dagerAvstand <= 1
    }
}
