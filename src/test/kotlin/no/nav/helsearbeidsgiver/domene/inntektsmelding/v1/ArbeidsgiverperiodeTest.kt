package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import no.nav.helsearbeidsgiver.utils.test.date.oktober
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

            withData(
                nameFn = { (tilfelle, _) -> "gir 'true' dersom AGP starter $tilfelle" },
                listOf(
                    "på andre dag i sykmeldingsperioden" to mockAgp(7.september til 22.september),
                    "på siste dag i sykmeldingsperioden" to mockAgp(30.september til 15.oktober),
                ),
            ) { (_, agpMedGyldigeDatoer) ->
                val sykmeldingsperioder = listOf(6.september til 30.september)

                agpMedGyldigeDatoer.erGyldigHvisIkkeForespurt(false, sykmeldingsperioder).shouldBeTrue()
            }

            withData(
                nameFn = { (tilfelle, _) -> "gir 'false' dersom AGP starter $tilfelle" },
                listOf(
                    "før sykmeldingsperioden starter" to mockAgp(4.september til 19.september),
                    "på samme dag som sykmeldingsperioden" to mockAgp(5.september til 20.september),
                    "etter sykmeldingsperioden slutter" to mockAgp(1.oktober til 16.oktober),
                ),
            ) { (_, agpMedUgyldigeDatoer) ->
                val sykmeldingsperioder = listOf(5.september til 30.september)

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
