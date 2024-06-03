package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

private const val MAKS_GRENSE_BELOEP = 1_000_000.0

internal fun String.erGyldigTlf(): Boolean =
    listOf(
        Regex("\\d{8}"),
        Regex("00\\d{10}"),
        Regex("\\+\\d{10}"),
    )
        .any(::matches)

internal fun Double.erStoerreEllerLikNullOgMindreEnnMaks(): Boolean =
    this >= 0 && this < MAKS_GRENSE_BELOEP

internal fun Double.erStoerreEnnNullOgMindreEnnMaks(): Boolean =
    this > 0 && this < MAKS_GRENSE_BELOEP
