package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.utils.test.date.mars

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
    })
