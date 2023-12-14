package no.nav.helsearbeidsgiver.domene.inntektsmelding

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun bestemmendeFravaersdag(
    arbeidsgiverperioder: List<Periode>,
    egenmeldingsperioder: List<Periode>,
    sykmeldingsperioder: List<Periode>,
): LocalDate {
    val sisteArbeidsgiverperiode = arbeidsgiverperioder
        .slaaSammenSammenhengendePerioder { denne, neste ->
            denne.tom.daysUntil(neste.fom) <= 1
        }
        .lastOrNull()

    val sisteSykdomsperiode = (egenmeldingsperioder + sykmeldingsperioder)
        .slaaSammenSammenhengendePerioder(
            kanSlaasSammen = ::kanSlaasSammenIgnorerHelgegap,
        )
        .filtrerBortUtgaaendePerioder()
        .last()

    return if (sisteArbeidsgiverperiode != null) {
        maxOf(
            sisteArbeidsgiverperiode.fom,
            sisteSykdomsperiode.fom,
        )
    } else {
        sisteSykdomsperiode.fom
    }
}

private fun List<Periode>.slaaSammenSammenhengendePerioder(
    kanSlaasSammen: (Periode, Periode) -> Boolean,
): List<Periode> =
    sortedBy { it.fom }
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

private fun List<Periode>.filtrerBortUtgaaendePerioder(): List<Periode> =
    filterIndexed { index, _ ->
        val antallForegaaendeDager = slice(0..<index)
            .sumOf {
                it.fom.daysUntil(it.tom) + 1
            }

        antallForegaaendeDager <= 16
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
