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
                val egenmeldingsperioder = listOf(8.september til 8.september)
                val sykmeldingsperioder = listOf(9.september til 30.september)
                val ugyldigIkkeForespurtAgp = mockAgp(8.september til 23.september)

                // Uten egenmeldinger
                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(false, emptyList(), sykmeldingsperioder).shouldBeFalse()
                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(true, emptyList(), sykmeldingsperioder).shouldBeTrue()

                // Med egenmeldinger
                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeFalse()
                ugyldigIkkeForespurtAgp.erGyldigHvisIkkeForespurt(true, egenmeldingsperioder, sykmeldingsperioder).shouldBeTrue()
            }

            test("gir 'true' dersom AGP er tom") {
                val egenmeldingsperioder = listOf(7.september til 7.september)
                val sykmeldingsperioder = listOf(8.september til 30.september)
                val tomAgp = mockAgp(null)

                tomAgp.erGyldigHvisIkkeForespurt(false, emptyList(), sykmeldingsperioder).shouldBeTrue()
                tomAgp.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeTrue()
            }

            test("gir 'true' dersom AGP fjerner rapporterte egenmeldinger og samtidig legger til nye") {
                val egenmeldingsperioder = listOf(6.september til 7.september)
                val sykmeldingsperioder = listOf(11.september til 30.september)
                val agpMedNyeEgenmeldinger = mockAgp(9.september til 24.september)

                agpMedNyeEgenmeldinger.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeTrue()
            }

            withData(
                "uten egenmeldinger" to false,
                "med egenmeldinger" to true,
            ) { (egenmeldingerTittel, medEgenmeldinger) ->
                withData(
                    nameFn = { (tilfelle, _) -> "gir 'true' dersom AGP starter $tilfelle ($egenmeldingerTittel)" },
                    listOf(
                        "på andre dag i sykefraværsperioden" to mockAgp(6.september til 21.september),
                        "på siste dag i sykefraværsperioden" to mockAgp(30.september til 15.oktober),
                    ),
                ) { (_, agpMedGyldigeDatoer) ->
                    val p1 = 5.september til 7.september
                    val p2 = 8.september til 30.september

                    val (egenmeldingsperioder, sykmeldingsperioder) =
                        if (medEgenmeldinger) {
                            listOf(p1) to listOf(p2)
                        } else {
                            emptyList<Periode>() to listOf(p1, p2)
                        }

                    agpMedGyldigeDatoer.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeTrue()
                }

                withData(
                    nameFn = { (tilfelle, _) -> "gir 'false' dersom AGP starter $tilfelle ($egenmeldingerTittel)" },
                    listOf(
                        "før sykefraværsperioden starter" to mockAgp(3.september til 18.september),
                        "på samme dag som sykefraværsperioden" to mockAgp(4.september til 19.september),
                        "etter sykmeldingsperioden slutter" to mockAgp(1.oktober til 16.oktober),
                    ),
                ) { (_, agpMedUgyldigeDatoer) ->
                    val p1 = 4.september til 5.september
                    val p2 = 6.september til 30.september

                    val (egenmeldingsperioder, sykmeldingsperioder) =
                        if (medEgenmeldinger) {
                            listOf(p1) to listOf(p2)
                        } else {
                            emptyList<Periode>() to listOf(p1, p2)
                        }

                    agpMedUgyldigeDatoer.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeFalse()
                }
            }

            withData(
                nameFn = { (tilfelle, _) -> "med egenmelding etter sykmelding, gir 'false' dersom AGP starter $tilfelle" },
                listOf(
                    "etter sykmeldingsperioden slutter, men før egenmeldingsperioden starter" to mockAgp(27.september til 12.oktober),
                    "på siste dag i egenmeldingsperioden" to mockAgp(30.september til 15.oktober),
                    "etter egenmeldingsperioden slutter" to mockAgp(1.oktober til 16.oktober),
                ),
            ) { (_, agpMedUgyldigeDatoer) ->
                val egenmeldingsperioder = listOf(28.september til 30.september)
                val sykmeldingsperioder = listOf(3.september til 25.september)

                agpMedUgyldigeDatoer.erGyldigHvisIkkeForespurt(false, egenmeldingsperioder, sykmeldingsperioder).shouldBeFalse()
            }
        }
    })

private fun mockAgp(periode: Periode?): Arbeidsgiverperiode =
    Arbeidsgiverperiode(
        perioder = listOfNotNull(periode),
        redusertLoennIAgp = null,
    )
