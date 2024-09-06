package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.agpPaavirkerIkkeInntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.daysUntil
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.slaaSammenSammenhengendePerioder
import java.time.LocalDate

fun bestemmendeFravaersdag(
    arbeidsgiverperioder: List<Periode>,
    sykefravaersperioder: List<Periode>, // sykmeldingsperioder (+ evt. egenmeldingsperioder)
): LocalDate {
    val agpSlutt = arbeidsgiverperioder.lastOrNull()?.tom
    val sykefravaersperioderStart = sykefravaersperioder.first().fom

    val sammenhengendeFravaer = sykefravaersperioder.slaaSammenSammenhengendePerioder(ignorerHelgegap = true)

    val sammenhengendeFravaersperioder = if (
        agpSlutt == null ||
        agpPaavirkerIkkeInntektsmelding(agpSlutt, sykefravaersperioderStart)
    ) {
        sammenhengendeFravaer
    } else {
        val sammenhengendeArbeidsgiverperioder =
            arbeidsgiverperioder.slaaSammenSammenhengendePerioder(ignorerHelgegap = false)

        val sammenhengendeFravaersperioderUtenAgp = sammenhengendeFravaer.fjernDatoerTilOgMed(agpSlutt)

        // Antar sykdom i helg i overgang fra AGP
        // Fremtidig versjon: Spør AG om sykdom i helg i overgang
        val overgangFraAgp = listOfNotNull(
            sammenhengendeArbeidsgiverperioder.last(),
            sammenhengendeFravaersperioderUtenAgp.firstOrNull(),
        )
            .slaaSammenSammenhengendePerioder(ignorerHelgegap = true)

        listOf(
            sammenhengendeArbeidsgiverperioder.dropLast(1),
            overgangFraAgp,
            sammenhengendeFravaersperioderUtenAgp.drop(1),
        )
            .flatten()
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

        antallForegaaendeDager <= AGP_MAKS_DAGER
    }
