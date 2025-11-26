package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.helsearbeidsgiver.utils.test.date.september

class ArbeidsgiverperiodeTest :
    FunSpec({
        context(Arbeidsgiverperiode::erGyldigHvisIkkeForespurt.name) {
            test("gir 'true' dersom AGP er forespurt") {
                val sykmeldingsperioder = listOf(8.september til 30.september)
                val ugyldigIkkeForespurtAgp = mockAgp(8.september til 23.september)

                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(false, sykmeldingsperioder).shouldBeFalse()
                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(true, sykmeldingsperioder).shouldBeTrue()
            }

            test("gir 'true' dersom AGP er tom") {
                val sykmeldingsperioder = listOf(7.september til 30.september)
                val tomAgp = mockAgp(null)

                tomAgp.erGyldigHvisIkkeForespurt(false, sykmeldingsperioder).shouldBeTrue()
            }

            test("gir 'true' dersom AGP starter etter sykmeldingsperioden") {
                val sykmeldingsperioder = listOf(6.september til 30.september)
                val agpMedGyldigeDatoer = mockAgp(7.september til 22.september)

                agpMedGyldigeDatoer.erGyldigHvisIkkeForespurt(false, sykmeldingsperioder).shouldBeTrue()
            }

            test("gir 'false' dersom AGP starter samtidig som sykmeldingsperioden") {
                val sykmeldingsperioder = listOf(5.september til 30.september)
                val agpMedUgyldigeDatoer = mockAgp(5.september til 20.september)

                agpMedUgyldigeDatoer.erGyldigHvisIkkeForespurt(false, sykmeldingsperioder).shouldBeFalse()
            }
        }
    })

private fun mockAgp(periode: Periode?): Arbeidsgiverperiode =
    Arbeidsgiverperiode(
        perioder = listOfNotNull(periode),
        egenmeldinger = emptyList(),
        redusertLoennIAgp = null,
    )
