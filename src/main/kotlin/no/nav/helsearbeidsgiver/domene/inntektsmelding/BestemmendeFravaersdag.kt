package no.nav.helsearbeidsgiver.domene.inntektsmelding

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun bestemmendeFravaersdag(
    arbeidsgiverperioder: List<Periode>,
    egenmeldingsperioder: List<Periode>,
    sykmeldingsperioder: List<Periode>,
): LocalDate =
    if (arbeidsgiverperioder.isNotEmpty()) {
        arbeidsgiverperioder
            .slaaSammenPerioder { denne, neste ->
                denne.tom.daysUntil(neste.fom) <= 1
            }
    } else {
        (egenmeldingsperioder + sykmeldingsperioder)
            .slaaSammenPerioder(
                kanSlaasSammen = ::kanSlaasSammenIgnorerHelgegap,
            )
    }
        .fom

private fun List<Periode>.slaaSammenPerioder(
    kanSlaasSammen: (Periode, Periode) -> Boolean,
): Periode =
    sortedBy { it.fom }
        .reduce { sammenhengende, neste ->
            if (kanSlaasSammen(sammenhengende, neste)) {
                Periode(
                    fom = sammenhengende.fom,
                    tom = maxOf(sammenhengende.tom, neste.tom),
                )
            } else {
                neste
            }
        }

private fun kanSlaasSammenIgnorerHelgegap(denne: Periode, neste: Periode): Boolean {
    val dagerAvstand = denne.tom.daysUntil(neste.fom)
    return when (denne.tom.dayOfWeek) {
        DayOfWeek.FRIDAY -> dagerAvstand <= 3
        DayOfWeek.SATURDAY -> dagerAvstand <= 2
        else -> dagerAvstand <= 1
    }
}

private fun LocalDate.daysUntil(other: LocalDate): Int =
    until(other, ChronoUnit.DAYS).toInt()
