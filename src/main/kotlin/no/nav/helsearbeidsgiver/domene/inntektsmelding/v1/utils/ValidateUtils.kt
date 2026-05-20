package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.InntektEndringAarsak

internal const val MAKS_GRENSE_BELOEP = 1_000_000.0

/* regex tillater tall \\d  og alle latinske tegn \\p{isLatin}, dvs A-Å og alle aksent-former med tødler etc.
*  Vi tillater whitespace \\s , punktum, komma, parantes og bindestrek. Må være mellom 1 og 128 tegn.
*  Siden yrkesbeskrivelse er noe vi tar imot i skjema,
*  men som egentlig ikke styres / velges av innsender (hentes fra AAreg), tillater vi blanke strenger også.
*  Sjekken er her først og fremst for å begrense muligheten for å sende inn søppel
*  i form av unødvendige og ugyldige verdier samt åpenbare spesialtegn som typisk brukes til enn XSS og SQL-injections
* */

internal fun String.erGyldigEllerBlankString(): Boolean =
    this.isBlank() ||
        this.matches("^[\\d\\p{IsLatin}\\s.,()-]{1,128}".toRegex())

internal fun String.erGyldigTlf(): Boolean =
    listOf(
        Regex("\\d{8}"),
        Regex("00\\d{10}"),
        Regex("\\+\\d{10}"),
    ).any(::matches)

internal fun Double.erStoerreEllerLikNullOgMindreEnnMaks(): Boolean = this >= 0 && this < MAKS_GRENSE_BELOEP

internal fun Double.erStoerreEnnNullOgMindreEnnMaks(): Boolean = this > 0 && this < MAKS_GRENSE_BELOEP

internal fun List<InntektEndringAarsak>?.harIngenDuplikater(): Boolean = this == null || distinct().size == size
