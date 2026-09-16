package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.desember
import no.nav.helsearbeidsgiver.utils.test.date.februar
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.test.date.mars
import no.nav.helsearbeidsgiver.utils.test.date.november
import no.nav.helsearbeidsgiver.utils.test.date.oktober
import no.nav.helsearbeidsgiver.utils.test.date.september
import java.time.format.DateTimeFormatter

private val datoMedUkedagFormat = DateTimeFormatter.ofPattern("E dd.MM")

class PeriodeUtilsKtTest :
    FunSpec({
        test("List<Periode>.tilDager()") {
            listOf(
                3.mars til 15.mars,
                21.mars til 23.mars,
                26.mars til 29.mars,
                27.mars til 31.mars, // overlapper med forrige
            ).tilDager() shouldContainExactly
                setOf(
                    3.mars,
                    4.mars,
                    5.mars,
                    6.mars,
                    7.mars,
                    8.mars,
                    9.mars,
                    10.mars,
                    11.mars,
                    12.mars,
                    13.mars,
                    14.mars,
                    15.mars,
                    21.mars,
                    22.mars,
                    23.mars,
                    26.mars,
                    27.mars,
                    28.mars,
                    29.mars,
                    30.mars,
                    31.mars,
                )
        }

        test("Set<LocalDate>.tilPerioder()") {
            setOf(
                6.mars,
                7.mars,
                8.mars,
                9.mars,
                10.mars,
                11.mars,
                12.mars,
                13.mars,
                14.mars,
                15.mars,
                21.mars,
                22.mars,
                23.mars, // fredag
                26.mars, // mandag
                27.mars,
                28.mars,
                29.mars,
                30.mars,
                31.mars,
            ).tilPerioder() shouldContainExactly
                listOf(
                    6.mars til 15.mars,
                    21.mars til 23.mars,
                    26.mars til 31.mars,
                )
        }

        context(::erSammenhengendeIgnorerHelgegap.name) {
            context("er sammenhengende") {
                withData(
                    nameFn = { (denne, neste) -> "${denne.tom.format(datoMedUkedagFormat)} er tilstøtende ${neste.fom.format(datoMedUkedagFormat)}" },
                    // hverdag
                    Pair(
                        1.januar til 3.januar,
                        4.januar til 6.januar,
                    ),
                    Pair(
                        9.juli til 26.juli,
                        27.juli til 12.august,
                    ),
                    Pair(
                        19.september til 9.oktober,
                        10.oktober til 27.oktober,
                    ),
                    // fredag mot lørdag
                    Pair(
                        4.januar til 12.januar,
                        13.januar til 24.januar,
                    ),
                    // lørdag mot søndag
                    Pair(
                        3.januar til 13.januar,
                        14.januar til 22.januar,
                    ),
                    // søndag mot mandag
                    Pair(
                        4.januar til 14.januar,
                        15.januar til 25.januar,
                    ),
                    // lørdagsgap
                    Pair(
                        11.januar til 12.januar,
                        14.januar til 17.januar,
                    ),
                    // søndagsgap
                    Pair(
                        11.januar til 13.januar,
                        15.januar til 19.januar,
                    ),
                    // helgegap
                    Pair(
                        11.januar til 12.januar,
                        15.januar til 18.januar,
                    ),
                    // månedsskifte
                    Pair(
                        20.januar til 31.januar,
                        1.februar til 15.februar,
                    ),
                    // årsskifte
                    Pair(
                        24.desember(2018) til 31.desember(2018),
                        1.januar(2019) til 10.januar(2019),
                    ),
                    // ikke skuddårsdag
                    Pair(
                        19.februar til 28.februar,
                        1.mars til 14.mars,
                    ),
                    // skuddårsdag
                    Pair(
                        19.februar(2024) til 28.februar(2024),
                        29.februar(2024) til 14.mars(2024),
                    ),
                    Pair(
                        19.februar(2024) til 29.februar(2024),
                        1.mars(2024) til 14.mars(2024),
                    ),
                ) { (denne, neste) ->
                    erSammenhengendeIgnorerHelgegap(denne, neste).shouldBeTrue()
                }
            }

            context("er _ikke_ sammenhengende") {
                withData(
                    nameFn = { (denne, neste) -> "${denne.tom.format(datoMedUkedagFormat)} er _ikke_ tilstøtende ${neste.fom.format(datoMedUkedagFormat)}" },
                    // kort gap
                    Pair(
                        1.mars til 5.mars,
                        7.mars til 16.mars,
                    ),
                    Pair(
                        4.juni til 18.juni,
                        21.juni til 30.juni,
                    ),
                    Pair(
                        2.desember til 20.desember,
                        22.desember til 29.desember,
                    ),
                    // langt gap
                    Pair(
                        7.mars til 12.mars,
                        28.mars til 10.april,
                    ),
                    Pair(
                        5.august til 12.august,
                        25.august til 6.september,
                    ),
                    Pair(
                        9.november til 23.november,
                        3.desember til 17.desember,
                    ),
                    // mandagsgap
                    Pair(
                        7.mars til 11.mars,
                        13.mars til 18.mars,
                    ),
                    // helgegap + mandagsgap
                    Pair(
                        6.mars til 9.mars,
                        13.mars til 21.mars,
                    ),
                    // fredagsgap
                    Pair(
                        4.mars til 8.mars,
                        10.mars til 23.mars,
                    ),
                    // fredagsgap + helgegap
                    Pair(
                        5.mars til 8.mars,
                        12.mars til 22.mars,
                    ),
                    // gap på akkurat én uke
                    Pair(
                        12.mars til 20.mars,
                        28.mars til 5.april,
                    ),
                    // gap på akkurat én måned
                    Pair(
                        21.mars til 26.mars,
                        27.april til 30.april,
                    ),
                    // gap på akkurat ett år
                    Pair(
                        16.mars(2018) til 23.mars(2018),
                        24.mars(2019) til 30.mars(2019),
                    ),
                    // gap over skuddårsdag
                    Pair(
                        19.februar(2024) til 28.februar(2024),
                        1.mars(2024) til 14.mars(2024),
                    ),
                    // feil rekkefølge, men sammenhengende
                    Pair(
                        16.mai til 19.mai,
                        9.mai til 15.mai,
                    ),
                    // feil rekkefølge og ikke sammenhengende
                    Pair(
                        16.mai til 19.mai,
                        8.mai til 10.mai,
                    ),
                    // like perioder
                    Pair(
                        14.mai til 22.mai,
                        14.mai til 22.mai,
                    ),
                    // delvis overlappende
                    Pair(
                        15.mai til 19.mai,
                        17.mai til 23.mai,
                    ),
                    // delvis overlappende, i feil rekkefølge
                    Pair(
                        17.mai til 23.mai,
                        15.mai til 19.mai,
                    ),
                    // fullstendig overlappende
                    Pair(
                        8.mai til 29.mai,
                        16.mai til 24.mai,
                    ),
                    // fullstendig overlappende, annen rekkefølge
                    Pair(
                        16.mai til 24.mai,
                        8.mai til 29.mai,
                    ),
                ) { (denne, neste) ->
                    erSammenhengendeIgnorerHelgegap(denne, neste).shouldBeFalse()
                }
            }
        }
    })
