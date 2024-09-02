package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import java.time.LocalDate

class PeriodeTest : FunSpec({

    val testFns =
        mapOf(
            "constructor" to ::Periode,
            "til" to { fom, tom -> fom til tom },
        )

    context("gyldig periode") {
        withData(testFns) { lagPeriodeFn ->
            checkAll<LocalDate, LocalDate> { a, b ->
                val datoer = listOf(a, b).sorted()

                val fom = datoer.first()
                val tom = datoer.last()

                val periode = lagPeriodeFn(fom, tom)

                periode.fom shouldBe fom
                periode.tom shouldBe tom
            }
        }
    }

    context("ugyldig periode") {
        withData(testFns) { lagPeriodeFn ->
            checkAll<LocalDate, LocalDate> { a, b ->
                val datoer = listOf(a, b).sorted()

                val fom = datoer.first()
                val tom = datoer.last()

                shouldThrowExactly<IllegalArgumentException> {
                    lagPeriodeFn(tom, fom)
                }
            }
        }
    }
})
