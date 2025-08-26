package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.desember
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mars
import no.nav.helsearbeidsgiver.utils.test.date.oktober

class PeriodeUtilsKtTest :
    FunSpec({

        context(::utledEgenmeldinger.name) {
            test("arbeidsgiverperioder påvirker ikke inntektsmelding - ingen egenmeldinger)") {
                val expected = emptyList<Periode>()

                val actual =
                    utledEgenmeldinger(
                        arbeidsgiverperioder =
                            listOf(
                                11.juni til 27.juni,
                            ),
                        sykmeldingsperioder =
                            listOf(
                                20.juli til 20.august,
                            ),
                    )

                actual shouldBe expected
            }

            test("ingen egenmeldinger") {
                val expected = emptyList<Periode>()

                val actual =
                    utledEgenmeldinger(
                        arbeidsgiverperioder =
                            listOf(
                                2.mars til 17.mars,
                            ),
                        sykmeldingsperioder =
                            listOf(
                                2.mars til 27.mars,
                            ),
                    )

                actual shouldBe expected
            }

            test("egenmeldinger før sykmeldingsperioder") {
                val expected =
                    listOf(
                        3.januar til 4.januar,
                        6.januar til 7.januar,
                    )

                val actual =
                    utledEgenmeldinger(
                        arbeidsgiverperioder =
                            listOf(
                                3.januar til 4.januar,
                                6.januar til 21.januar,
                            ),
                        sykmeldingsperioder =
                            listOf(
                                8.januar til 31.januar,
                            ),
                    )

                actual shouldBe expected
            }

            test("egenmeldinger mellom sykmeldingsperioder") {
                val expected =
                    listOf(
                        1.oktober til 1.oktober,
                        4.oktober til 6.oktober,
                        11.oktober til 14.oktober,
                    )

                val actual =
                    utledEgenmeldinger(
                        arbeidsgiverperioder =
                            listOf(
                                1.oktober til 16.oktober,
                            ),
                        sykmeldingsperioder =
                            listOf(
                                2.oktober til 3.oktober,
                                7.oktober til 10.oktober,
                                15.oktober til 22.oktober,
                            ),
                    )

                actual shouldBe expected
            }

            test("helgegap i egenmeldinger tettes _ikke_") {
                val expected =
                    listOf(
                        2.desember til 7.desember,
                        10.desember til 12.desember,
                    )

                val actual =
                    utledEgenmeldinger(
                        arbeidsgiverperioder =
                            listOf(
                                // 7. desember 2018 er en fredag
                                2.desember til 7.desember,
                                10.desember til 19.desember,
                            ),
                        sykmeldingsperioder =
                            listOf(
                                13.desember til 31.desember,
                            ),
                    )

                actual shouldBe expected
            }

//          TODO: Skrive ferdig test
            test("behandlingsdager blir godkjent"){
                1 shouldBe 0
            }
        }
    })
