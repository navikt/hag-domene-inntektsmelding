package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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

class BestemmendeFravaersdagKtTest : FunSpec({

    context("arbeidsgiverperiode påvirker ikke sykmelding") {

        test("overser alle arbeidsgiverperioder når siste dag har gap på _mer_ enn 16 dager til sykmeldingsperioder") {
            val expected = 8.juni

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    11.mars til 20.mars,
                    22.mars til 29.mars,
                ),
                sykmeldingsperioder = listOf(
                    8.juni til 29.juni,
                ),
            )

            actual shouldBe expected
        }

        test("kun én sykmeldingperiode") {
            val expected = 17.september

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    17.september til 31.oktober,
                ),
            )

            actual shouldBe expected
        }

        test("flere sykmeldingsperioder uten gap") {
            val expected = 21.juni

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    21.juni til 1.juli,
                    2.juli til 13.juli,
                    14.juli til 25.juli,
                ),
            )

            actual shouldBe expected
        }

        test("flere sykmeldingsperioder med helgegap") {
            val expected = 2.august

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    // 3. august 2018 er en fredag
                    2.august til 3.august,
                    6.august til 29.august,
                ),
            )

            actual shouldBe expected
        }

        test("flere sykmeldingsperioder med hverdagsgap") {
            val expected = 14.november

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    // 11. november 2018 er en søndag
                    5.november til 11.november,
                    14.november til 28.november,
                ),
            )

            actual shouldBe expected
        }

        test("tåler overlappende sykmeldingsperioder (skal ikke skje)") {
            val expected = 9.juli

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    9.juli til 19.juli,
                    15.juli til 23.juli,
                ),
            )

            actual shouldBe expected
        }
    }

    context("arbeidsgiverperiode påvirker sykmelding") {

        context("sammenslåing av arbeidsgiverperioder (for sikkerhets skyld, skjer også i frontend)") {

            test("arbeidsgiverperioder uten gap") {
                val expected = 2.juli

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        2.juli til 2.juli,
                        3.juli til 4.juli,
                    ),
                    sykmeldingsperioder = listOf(
                        5.juli til 25.juli,
                        26.juli til 29.juli,
                        30.juli til 12.august,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder med helgegap") {
                val expected = 12.august

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        // 10. august 2018 er en fredag
                        7.august til 10.august,
                        12.august til 13.august,
                    ),
                    sykmeldingsperioder = listOf(
                        // 17 august 2018 er en fredag
                        14.august til 17.august,
                        20.august til 19.september,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder med hverdagsgap") {
                val expected = 10.desember

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        // 6. desember 2018 er en torsdag
                        3.desember til 6.desember,
                        10.desember til 16.desember,
                    ),
                    sykmeldingsperioder = listOf(
                        17.desember til 29.desember,
                    ),
                )

                actual shouldBe expected
            }
        }

        context("arbeidsgiverperioder uten overlapp med sykmeldingsperioder") {

            test("arbeidsgiverperioder med gap på _mindre_ enn 16 dager til sykmeldingsperioder") {
                val expected = 10.januar

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        10.januar til 26.januar,
                    ),
                    sykmeldingsperioder = listOf(
                        7.februar til 28.februar,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder uten gap til sykmeldingperioder") {
                val expected = 2.mai

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        2.mai til 2.mai,
                    ),
                    sykmeldingsperioder = listOf(
                        3.mai til 25.mai,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder med helgegap til sykmeldingperioder") {
                val expected = 5.mars

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        // 3. mars 2018 er en lørdag
                        2.mars til 3.mars,
                    ),
                    sykmeldingsperioder = listOf(
                        5.mars til 30.mars,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder med hverdagsgap til sykmeldingperioder") {
                val expected = 9.januar

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        // 7. januar 2018 er en mandag
                        7.januar til 7.januar,
                    ),
                    sykmeldingsperioder = listOf(
                        9.januar til 29.januar,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder ekskluderer siste sykmeldingsperioder (utgår fra inntektsmeldingsgrunnlag)") {
                val expected = 11.august

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        23.juli til 29.juli,
                    ),
                    sykmeldingsperioder = listOf(
                        1.august til 5.august,
                        6.august til 8.august,
                        11.august til 12.august,
                        // Ekskluderes _ikke_ pga. manglende gap, men påvirker derfor heller ikke bestemmende fraværsdag
                        13.august til 14.august,
                        // Resten skal bli ekskludert da foregående perioder er over 16 dager
                        17.august til 18.august,
                        19.august til 20.august,
                        25.august til 30.august,
                    ),
                )

                actual shouldBe expected
            }
        }

        context("arbeidsgiverperioder med overlapp med sykmeldingsperioder") {

            test("arbeidsgiverperiodene starter _samtidig_ som sykmeldingsperioder (ingen gap)") {
                val expected = 1.januar

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        1.januar til 16.januar,
                    ),
                    sykmeldingsperioder = listOf(
                        1.januar til 31.januar,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder starter _før_ sykmeldingsperioder (ingen gap)") {
                val expected = 2.oktober

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        2.oktober til 17.oktober,
                    ),
                    sykmeldingsperioder = listOf(
                        4.oktober til 27.oktober,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder starter _etter_ sykmeldingsperioder (ingen gap)") {
                val expected = 3.juli

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        3.juli til 18.juli,
                    ),
                    sykmeldingsperioder = listOf(
                        1.juli til 27.juli,
                    ),
                )

                actual shouldBe expected
            }

            test("gap i arbeidsgiverperioder overstyrer sammenhengende sykmeldingsperioder") {
                val expected = 16.september

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        7.september til 12.september,
                        16.september til 20.september,
                    ),
                    sykmeldingsperioder = listOf(
                        2.september til 30.september,
                    ),
                )

                actual shouldBe expected
            }

            test("gap i sykmeldingsperioder tettes av sammenhengende arbeidsgiverperioder") {
                val expected = 11.desember

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        11.desember til 27.desember,
                    ),
                    sykmeldingsperioder = listOf(
                        9.desember til 20.desember,
                        24.desember til 31.desember,
                    ),
                )

                actual shouldBe expected
            }

            test("gap i sykmeldingsperioder etter arbeidsgiverperioder") {
                val expected = 22.mai

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        4.mai til 10.mai,
                    ),
                    sykmeldingsperioder = listOf(
                        4.mai til 19.mai,
                        22.mai til 29.mai,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder ekskluderer siste sykmeldingsperioder (utgår fra inntektsmeldingsgrunnlag)") {
                val expected = 9.mars

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        9.mars til 28.mars,
                    ),
                    sykmeldingsperioder = listOf(
                        10.mars til 20.mars,
                        25.mars til 29.mars,
                        // Skal bli ekskludert da foregående perioder er over 16 dager
                        2.april til 5.april,
                    ),
                )

                actual shouldBe expected
            }

            test("arbeidsgiverperioder ekskluderer _ikke_ siste sykmeldingsperioder (utgår fra inntektsmeldingsgrunnlag) når gap kompenserer for egenmeldinger") {
                val expected = 2.april

                val actual = bestemmendeFravaersdag(
                    arbeidsgiverperioder = listOf(
                        7.mars til 14.mars,
                        18.mars til 20.mars,
                        25.mars til 29.mars,
                    ),
                    sykmeldingsperioder = listOf(
                        10.mars til 20.mars,
                        25.mars til 29.mars,
                        2.april til 5.april,
                    ),
                )

                actual shouldBe expected
            }
        }
    }

    test("tåler usorterte arbeidsgiver- og sykmeldingsperioder") {
        val expected = 2.februar

        val actual = bestemmendeFravaersdag(
            arbeidsgiverperioder = listOf(
                4.februar til 4.februar,
                2.februar til 3.februar,
            ),
            sykmeldingsperioder = listOf(
                13.februar til 19.februar,
                20.februar til 27.februar,
                4.februar til 13.februar,
                28.februar til 28.februar,
            ),
        )

        actual shouldBe expected
    }

    test("kaster exception når sykmeldingsperioder mangler") {
        shouldThrowExactly<NoSuchElementException> {
            bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    1.juli til 16.juli,
                ),
                sykmeldingsperioder = emptyList(),
            )
        }
    }
})
