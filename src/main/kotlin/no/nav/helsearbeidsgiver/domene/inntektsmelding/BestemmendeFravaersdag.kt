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
        arbeidsgiverperioder.maxOf(Periode::fom)
    } else {
        (egenmeldingsperioder + sykmeldingsperioder)
            .sortedBy { it.fom }
            .reduce { sammenhengende, neste ->
                if (sammenhengende.kanSlaasSammenMed(neste)) {
                    Periode(
                        fom = sammenhengende.fom,
                        tom = maxOf(sammenhengende.tom, neste.tom),
                    )
                } else {
                    neste
                }
            }
            .fom
    }

private fun Periode.kanSlaasSammenMed(other: Periode): Boolean {
    val dagerAvstand = tom.daysUntil(other.fom)
    return when (tom.dayOfWeek) {
        DayOfWeek.FRIDAY -> dagerAvstand <= 3
        DayOfWeek.SATURDAY -> dagerAvstand <= 2
        else -> dagerAvstand <= 1
    }
}

private fun LocalDate.daysUntil(other: LocalDate): Int =
    until(other, ChronoUnit.DAYS).toInt()
