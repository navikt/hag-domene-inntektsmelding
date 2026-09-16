package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val PERIODE_GAP_MAKS_DAGER = 16

fun List<Periode>.tilDager(): Set<LocalDate> =
    flatMap {
        List(it.antallDager()) { index ->
            it.fom.plusDays(index.toLong())
        }
    }.toSet()

fun Set<LocalDate>.tilPerioder(): List<Periode> =
    map { Periode(it, it) }
        .slaaSammenSammenhengendePerioder(ignorerHelgegap = false)

fun erSammenhengendeIgnorerHelgegap(
    denne: Periode,
    neste: Periode,
): Boolean {
    val dagerAvstand = denne.tom.daysUntil(neste.fom)

    val maksAvstand =
        when (denne.tom.dayOfWeek) {
            DayOfWeek.FRIDAY -> 3
            DayOfWeek.SATURDAY -> 2
            else -> 1
        }

    return dagerAvstand in 1..maksAvstand
}

internal fun Periode.antallDager(): Int = fom.daysUntil(tom) + 1

internal fun agpPaavirkerIkkeInntektsmelding(
    agpSlutt: LocalDate,
    sykmeldingsperioderStart: LocalDate,
): Boolean = agpSlutt.daysUntil(sykmeldingsperioderStart) > PERIODE_GAP_MAKS_DAGER

/** Slår ikke sammen overlappende perioder eller perioder i feil rekkefølge. */
internal fun List<Periode>.slaaSammenSammenhengendePerioder(ignorerHelgegap: Boolean): List<Periode> {
    val kanSlaasSammen =
        if (ignorerHelgegap) {
            ::erSammenhengendeIgnorerHelgegap
        } else {
            ::erSammenhengende
        }

    return sortedBy { it.fom }
        .fold(emptyList()) { slaattSammen, periode ->
            val forrige = slaattSammen.lastOrNull()

            if (forrige != null && kanSlaasSammen(forrige, periode)) {
                val sammenhengende =
                    Periode(
                        fom = forrige.fom,
                        tom = maxOf(forrige.tom, periode.tom),
                    )

                slaattSammen.dropLast(1).plus(sammenhengende)
            } else {
                slaattSammen.plus(periode)
            }
        }
}

private fun LocalDate.daysUntil(other: LocalDate): Int = until(other, ChronoUnit.DAYS).toInt()

private fun erSammenhengende(
    denne: Periode,
    neste: Periode,
): Boolean = denne.tom.daysUntil(neste.fom) == 1
