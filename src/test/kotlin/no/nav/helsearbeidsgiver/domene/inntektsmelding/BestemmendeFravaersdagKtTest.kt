package no.nav.helsearbeidsgiver.domene.inntektsmelding

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

    context("arbeidsgiverperiode tilstede") {

        test("kun én arbeidsgiverperiode") {
            val expected = 1.januar

            // Arbeidsgiver overstyrer AGP
            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    1.januar til 16.januar,
                ),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    4.januar til 31.januar,
                ),
            )

            actual shouldBe expected
        }

        test("flere arbeidsgiverperioder kant i kant behandles som enkelt arbeidsgiverperiode") {
            val expected = 1.februar

            // Arbeidsgiver overstyrer AGP
            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    1.februar til 12.februar,
                    13.februar til 16.februar,
                ),
                egenmeldingsperioder = listOf(
                    3.februar til 5.februar,
                ),
                sykmeldingsperioder = listOf(
                    6.februar til 28.februar,
                ),
            )

            actual shouldBe expected
        }

        test("flere arbeidsgiverperioder med hverdagsgap") {
            val expected = 10.mars

            // Arbeidsgiver overstyrer AGP
            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    // 8. mars 2018 er en torsdag
                    1.mars til 8.mars,
                    10.mars til 17.mars,
                ),
                egenmeldingsperioder = listOf(
                    6.mars til 7.mars,
                ),
                sykmeldingsperioder = listOf(
                    11.mars til 31.mars,
                ),
            )

            actual shouldBe expected
        }

        test("flere arbeidsgiverperioder med helgegap") {
            val expected = 15.april

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    // 13. april 2018 er en fredag
                    1.april til 13.april,
                    15.april til 17.april,
                ),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    15.april til 17.april,
                ),
            )

            actual shouldBe expected
        }

        test("arbeidsgiverperiode kant i kant med sykmeldingsperioder") {
            val expected = 5.januar

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    5.januar til 21.januar,
                ),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    22.januar til 22.februar,
                ),
            )

            actual shouldBe expected
        }

        test("arbeidsgiverperiode med gap til egenmeldingsperioder") {
            // Kommenter inn når logikk endres til å velge fom fra egenmeldingsperioder
//            val expected = 3.februar

            val expected = 10.januar

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    10.januar til 26.januar,
                ),
                egenmeldingsperioder = listOf(
                    3.februar til 6.februar,
                ),
                sykmeldingsperioder = listOf(
                    7.februar til 28.februar,
                ),
            )

            actual shouldBe expected
        }

        test("arbeidsgiverperiode med gap til sykmeldingsperioder") {
            // Kommenter inn når logikk endres til å velge fom fra sykmeldingsperioder
//            val expected = 8.juni

            val expected = 13.mars

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = listOf(
                    13.mars til 29.mars,
                ),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    8.juni til 29.juni,
                ),
            )

            actual shouldBe expected
        }
    }

    context("arbeidsgiverperoide ikke tilstede") {

        test("uten egenmeldinger, kun én sykmeldingperiode") {
            val expected = 17.september

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    17.september til 31.oktober,
                ),
            )

            actual shouldBe expected
        }

        test("uten egenmeldinger, flere sykmeldingperioder uten gap") {
            val expected = 21.juni

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    21.juni til 1.juli,
                    2.juli til 25.juli,
                ),
            )

            actual shouldBe expected
        }

        test("uten egenmeldinger, flere sykmeldingperioder med helgegap") {
            val expected = 2.august

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    // 3. august 2018 er en torsdag
                    2.august til 3.august,
                    6.august til 29.august,
                ),
            )

            actual shouldBe expected
        }

        test("uten egenmeldinger, flere sykmeldingperioder med hverdagsgap") {
            val expected = 14.november

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = emptyList(),
                sykmeldingsperioder = listOf(
                    // 11. november 2018 er en søndag
                    5.november til 11.november,
                    14.november til 28.november,
                ),
            )

            actual shouldBe expected
        }

        test("kun én egenmeldingsperiode, uten gap til enkelt sykmeldingperiode") {
            val expected = 2.mai

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    2.mai til 2.mai,
                ),
                sykmeldingsperioder = listOf(
                    3.mai til 25.mai,
                ),
            )

            actual shouldBe expected
        }

        test("kun én egenmeldingsperiode, med helgegap til enkelt sykmeldingperiode") {
            val expected = 2.mars

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    // 3. mars 2018 er en lørdag
                    2.mars til 3.mars,
                ),
                sykmeldingsperioder = listOf(
                    5.mars til 30.mars,
                ),
            )

            actual shouldBe expected
        }

        test("kun én egenmeldingsperiode, med hverdagsgap til enkelt sykmeldingperiode") {
            val expected = 9.januar

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    // 7. januar 2018 er en mandag
                    7.januar til 7.januar,
                ),
                sykmeldingsperioder = listOf(
                    9.januar til 29.januar,
                ),
            )

            actual shouldBe expected
        }

        test("flere egenmeldings- og sykmeldingperioder uten gap") {
            val expected = 2.juli

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
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

        test("flere egenmeldings- og sykmeldingperioder med helgegap") {
            val expected = 7.august

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    // 10. august 2018 er en fredag
                    7.august til 10.august,
                    // 17. august 2018 er en fredag
                    12.august til 17.august,
                ),
                sykmeldingsperioder = listOf(
                    20.august til 31.august,
                    1.september til 19.september,
                ),
            )

            actual shouldBe expected
        }

        test("flere egenmeldings- og sykmeldingperioder med hverdagsgap") {
            val expected = 10.desember

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    // 6. desember 2018 er en torsdag
                    3.desember til 6.desember,
                    10.desember til 16.desember,
                ),
                sykmeldingsperioder = listOf(
                    17.desember til 20.desember,
                    21.desember til 29.desember,
                ),
            )

            actual shouldBe expected
        }

        test("tåler usorterte egenmeldings- og sykmeldingsperioder") {
            val expected = 2.februar

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
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

        test("tåler egenmeldingsperioder mellom sykmeldingsperiodene") {
            val expected = 6.april

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    6.april til 9.april,
                    19.april til 21.april,
                ),
                sykmeldingsperioder = listOf(
                    10.april til 18.april,
                    22.april til 29.april,
                ),
            )

            actual shouldBe expected
        }

        test("tåler overlappende egenmeldings- og sykmeldingsperioder") {
            val expected = 1.oktober

            val actual = bestemmendeFravaersdag(
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = listOf(
                    1.oktober til 5.oktober,
                    4.oktober til 7.oktober,
                ),
                sykmeldingsperioder = listOf(
                    6.oktober til 14.oktober,
                    7.oktober til 10.oktober,
                    7.oktober til 19.oktober,
                    19.oktober til 24.oktober,
                ),
            )

            actual shouldBe expected
        }
    }
})
