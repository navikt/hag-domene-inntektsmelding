package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.agpPaavirkerIkkeInntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.daysUntil
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.slaaSammenSammenhengendePerioder
import java.time.LocalDate

private const val AGP_MAKS_DAGER = 16

fun bestemmendeFravaersdag(
    arbeidsgiverperioder: List<Periode>,
    sykmeldingsperioder: List<Periode>,
): LocalDate {
    val agpSlutt = arbeidsgiverperioder.lastOrNull()?.tom
    val sykmeldingsperioderStart = sykmeldingsperioder.first().fom

    val sammenhengendeSykmeldingsperioder = sykmeldingsperioder.slaaSammenSammenhengendePerioder(ignorerHelgegap = true)

    val sammenhengendeFravaersperioder = if (
        agpSlutt == null ||
        agpPaavirkerIkkeInntektsmelding(agpSlutt, sykmeldingsperioderStart)
    ) {
        sammenhengendeSykmeldingsperioder
    } else {
        val sammenhengendeArbeidsgiverperioder =
            arbeidsgiverperioder.slaaSammenSammenhengendePerioder(ignorerHelgegap = false)

        val sammenhengendeSykmeldingsperioderUtenAgp = sammenhengendeSykmeldingsperioder.fjernDatoerTilOgMed(agpSlutt)

        // Antar sykdom i helg i overgang fra AGP
        // Fremtidig versjon: Spør AG om sykdom i helg i overgang
        val overgangFraAgp = listOf(
            sammenhengendeArbeidsgiverperioder.last(),
            sammenhengendeSykmeldingsperioderUtenAgp.first(),
        )
            .slaaSammenSammenhengendePerioder(ignorerHelgegap = true)

        listOf(
            sammenhengendeArbeidsgiverperioder.dropLast(1),
            overgangFraAgp,
            sammenhengendeSykmeldingsperioderUtenAgp.drop(1),
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
