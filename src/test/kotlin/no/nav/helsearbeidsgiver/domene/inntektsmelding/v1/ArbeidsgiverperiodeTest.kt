package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.desember
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.test.date.mars
import no.nav.helsearbeidsgiver.utils.test.date.oktober
import no.nav.helsearbeidsgiver.utils.test.date.september

class ArbeidsgiverperiodeTest :
    FunSpec({
        context(Arbeidsgiverperiode::validerMotSykmeldingsperioder.name) {

            context("gyldig dersom AGP er tom") {
                val tomAgp = mockAgp()
                val sykmeldingsperioder = listOf(8.september til 30.september)

                medVarierendeData(
                    egenmeldingerFraForespoersel = listOf(7.september til 7.september),
                ) { erAgpForespurt, egenmeldingerFraForespoersel ->
                    tomAgp
                        .validerMotSykmeldingsperioder(
                            erAgpForespurt = erAgpForespurt,
                            egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                            sykmeldingsperioder = sykmeldingsperioder,
                        ).shouldBeEmpty()
                }
            }

            context("gyldige egenmeldinger i AGP") {
                // AGP sjekkes for lengde _før_ vi kaller 'agp.erGyldig', så vi slipper unna med for lang AGP her
                val agpMedGyldigeEgenmeldiger =
                    mockAgp(
                        2.april til 3.april,
                        5.april til 7.april, // egenmelding
                        10.april til 12.april,
                        14.april til 14.april, // egenmelding
                        17.april til 20.april,
                        22.april til 23.april, // egenmelding
                        26.april til 28.april,
                        30.april til 8.mai, // egenmelding
                        10.mai til 15.mai,
                        17.mai til 25.mai, // egenmelding i start
                        29.mai til 2.juni, // lør 2. fjernes
                        7.juni til 10.juni, // lør 9. og søn 10. fjernes
                        12.juni til 19.juni, // lør 16. og søn 17. fjernes
                        21.juni til 25.juni, // søn 24. fjernes
                        28.juni til 29.juni,
                        30.juni til 1.juli, // lør 30. og søn 1. fjernes
                        4.juli til 6.juli,
                        7.juli til 11.juli, // lør 7. og søn 8. fjernes
                        18.juli til 19.juli, // 18. er gyldig fordi sykmeldingsdag 17. fjernes
                        23.juli til 23.juli,
                        25.juli til 26.juli, // 25. er gyldig fordi sykmeldingsdag 24. fjernes
                    )
                val sykmeldingsperioder =
                    listOf(
                        // starter før AGP for å oppfylle 'erGyldigSomIkkeForespurt'
                        1.april til 3.april,
                        10.april til 12.april,
                        17.april til 20.april,
                        26.april til 28.april,
                        10.mai til 15.mai,
                        21.mai til 25.mai,
                        29.mai til 1.juni,
                        7.juni til 8.juni,
                        12.juni til 15.juni,
                        21.juni til 23.juni,
                        28.juni til 29.juni,
                        4.juli til 6.juli,
                        16.juli til 17.juli, // fjernes i AGP
                        23.juli til 24.juli, // 24. fjernes i AGP
                    )

                medVarierendeData(
                    egenmeldingerFraForespoersel = listOf(4.april til 7.april),
                ) { erAgpForespurt, egenmeldingerFraForespoersel ->
                    agpMedGyldigeEgenmeldiger
                        .validerMotSykmeldingsperioder(
                            erAgpForespurt = erAgpForespurt,
                            egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                            sykmeldingsperioder = sykmeldingsperioder,
                        ).shouldBeEmpty()
                }
            }

            context("ugyldige egenmeldinger i AGP") {
                val sykmeldingsperioder =
                    listOf(
                        // starter før AGP for å oppfylle 'erGyldigSomIkkeForespurt'
                        1.april til 3.april,
                        10.april til 12.april,
                        17.april til 22.april,
                        26.april til 2.mai,
                        10.mai til 15.mai,
                        21.mai til 28.mai,
                        4.juni til 6.juni,
                        8.juni til 11.juni,
                        21.juni til 24.juni,
                        2.juli til 3.juli,
                        9.juli til 11.juli,
                    )

                withData(
                    nameFn = { "AGP=${it.perioder}" },
                    mockAgp(2.april til 4.april), // 4. er ikke gyldig
                    mockAgp(9.april til 15.april), // 13. er ikke gyldig
                    mockAgp(17.april til 24.april), // 23. er ikke gyldig
                    mockAgp(26.april til 15.mai), // 3. er ikke gyldig
                    mockAgp(
                        29.mai til 31.mai, // 29. er ikke gyldig
                        21.mai til 28.mai,
                    ),
                    mockAgp(
                        4.juni til 6.juni,
                        7.juni til 7.juni, // 7. er ikke gyldig
                        8.juni til 11.juni,
                    ),
                    mockAgp(
                        8.juni til 11.juni,
                        12.juni til 18.juni, // 12. er ikke gyldig
                    ),
                    mockAgp(
                        8.juni til 11.juni,
                        12.juni til 12.juni, // 12. er ikke gyldig
                        13.juni til 18.juni,
                    ),
                    mockAgp(
                        21.juni til 24.juni,
                        25.juni til 27.juni, // 25. er ikke gyldig
                    ),
                    mockAgp(3.juli til 5.juli), // 4. er ikke gyldig
                    mockAgp(
                        11.juli til 11.juli,
                        12.juli til 13.juli, // 12. er ikke gyldig
                    ),
                ) { agpMedUgyldigeEgenmeldinger ->

                    medVarierendeData(
                        egenmeldingerFraForespoersel = listOf(4.april til 7.april),
                    ) { erAgpForespurt, egenmeldingerFraForespoersel ->
                        agpMedUgyldigeEgenmeldinger
                            .validerMotSykmeldingsperioder(
                                erAgpForespurt = erAgpForespurt,
                                egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                                sykmeldingsperioder = sykmeldingsperioder,
                            ) shouldContainExactly setOf(Feilmelding.AGP_EGENMELDING_ETTER_GJENOPPTATT_ARBEID)
                    }
                }
            }

            test("gyldig dersom AGP er forespurt") {
                val ugyldigIkkeForespurtAgp = mockAgp(8.september til 23.september)
                val sykmeldingsperioder = listOf(9.september til 30.september)
                val egenmeldingerFraForespoersel = listOf(8.september til 8.september)

                // Uten egenmeldinger
                ugyldigIkkeForespurtAgp
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = false,
                        egenmeldingerFraForespoersel = emptyList(),
                        sykmeldingsperioder = sykmeldingsperioder,
                    ) shouldContainExactly setOf(Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER)

                ugyldigIkkeForespurtAgp
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = true,
                        egenmeldingerFraForespoersel = emptyList(),
                        sykmeldingsperioder = sykmeldingsperioder,
                    ).shouldBeEmpty()

                // Med egenmeldinger
                ugyldigIkkeForespurtAgp
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = false,
                        egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                        sykmeldingsperioder = sykmeldingsperioder,
                    ) shouldContainExactly setOf(Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER)

                ugyldigIkkeForespurtAgp
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = true,
                        egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                        sykmeldingsperioder = sykmeldingsperioder,
                    ).shouldBeEmpty()
            }

            test("gyldig dersom AGP fjerner rapporterte egenmeldinger og samtidig legger til nye") {
                val agpMedNyeEgenmeldinger = mockAgp(9.september til 24.september)
                val sykmeldingsperioder = listOf(11.september til 30.september)
                val egenmeldingerFraForespoersel = listOf(6.september til 7.september)

                agpMedNyeEgenmeldinger
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = false,
                        egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                        sykmeldingsperioder = sykmeldingsperioder,
                    ).shouldBeEmpty()
            }

            withData(
                nameFn = { (_, egenmeldinger) -> "gyldig dersom AGP (med ${egenmeldinger.size} egenmeldinger) starter..." },
                listOf(5.september til 7.september, 8.september til 30.september) to emptyList(),
                listOf(8.september til 30.september) to listOf(5.september til 7.september),
            ) { (sykmeldingsperioder, egenmeldingerFraForespoersel) ->
                withData(
                    mapOf(
                        "på andre dag i sykefraværsperioden" to mockAgp(6.september til 21.september),
                        // 01.10 er ikke gyldig egenmeldingsdag
                        "på siste dag i sykefraværsperioden" to mockAgp(30.september til 30.september, 2.oktober til 16.oktober),
                    ),
                ) { agpMedGyldigeDatoer ->
                    agpMedGyldigeDatoer
                        .validerMotSykmeldingsperioder(
                            erAgpForespurt = false,
                            egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                            sykmeldingsperioder = sykmeldingsperioder,
                        ).shouldBeEmpty()
                }
            }

            withData(
                nameFn = { (_, egenmeldinger) -> "ugyldig dersom AGP (med ${egenmeldinger.size} egenmeldinger) starter..." },
                listOf(4.september til 5.september, 6.september til 30.september) to emptyList(),
                listOf(6.september til 30.september) to listOf(4.september til 5.september),
            ) { (sykmeldingsperioder, egenmeldingerFraForespoersel) ->
                withData(
                    mapOf(
                        "før sykefraværsperioden starter" to mockAgp(3.september til 18.september),
                        "på samme dag som sykefraværsperioden" to mockAgp(4.september til 19.september),
                        "etter sykmeldingsperioden slutter" to mockAgp(1.oktober til 16.oktober),
                    ),
                ) { agpMedUgyldigeDatoer ->
                    agpMedUgyldigeDatoer
                        .validerMotSykmeldingsperioder(
                            erAgpForespurt = false,
                            egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                            sykmeldingsperioder = sykmeldingsperioder,
                        ) shouldContainExactly setOf(Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER)
                }
            }

            withData(
                nameFn = { (tilfelle, _) -> "med egenmelding etter sykmelding, ugyldig dersom AGP starter $tilfelle" },
                listOf(
                    "etter sykmeldingsperioden slutter, men før egenmeldingsperioden starter" to mockAgp(27.september til 12.oktober),
                    "på siste dag i egenmeldingsperioden" to mockAgp(30.september til 15.oktober),
                    "etter egenmeldingsperioden slutter" to mockAgp(1.oktober til 16.oktober),
                ),
            ) { (_, agpMedUgyldigeDatoer) ->
                val sykmeldingsperioder = listOf(3.september til 25.september)
                val egenmeldingerFraForespoersel = listOf(28.september til 30.september)

                agpMedUgyldigeDatoer
                    .validerMotSykmeldingsperioder(
                        erAgpForespurt = false,
                        egenmeldingerFraForespoersel = egenmeldingerFraForespoersel,
                        sykmeldingsperioder = sykmeldingsperioder,
                    ) shouldContainExactly setOf(Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER)
            }

            test("begge feilmeldinger samtidig") {
                val agp = mockAgp(3.september til 18.september)
                val sykmeldingsperioder =
                    listOf(
                        3.september til 11.september,
                        17.september til 30.september,
                    )

                agp.validerMotSykmeldingsperioder(
                    erAgpForespurt = false,
                    egenmeldingerFraForespoersel = emptyList(),
                    sykmeldingsperioder = sykmeldingsperioder,
                ) shouldContainExactly
                    setOf(
                        Feilmelding.AGP_EGENMELDING_ETTER_GJENOPPTATT_ARBEID,
                        Feilmelding.AGP_IKKE_FORESPURT_KREVER_ARBEID_I_START_AV_SYKEFRAVAER,
                    )
            }
        }

        context(Arbeidsgiverperiode::utledEgenmeldinger.name) {
            test("tom AGP gir ingen egenmeldinger") {
                val agp = mockAgp()
                val sykmeldingsperioder = listOf(7.mars til 29.april)

                val expected = emptyList<Periode>()

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("arbeidsgiverperioder påvirker ikke inntektsmelding - ingen egenmeldinger") {
                val agp = mockAgp(11.juni til 27.juni)
                val sykmeldingsperioder = listOf(20.juli til 20.august)

                val expected = emptyList<Periode>()

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("ingen egenmeldinger") {
                val agp = mockAgp(2.mars til 17.mars)
                val sykmeldingsperioder = listOf(2.mars til 27.mars)

                val expected = emptyList<Periode>()

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("egenmeldinger før sykmeldingsperioder") {
                val agp =
                    mockAgp(
                        3.januar til 4.januar,
                        8.januar til 23.januar,
                    )
                val sykmeldingsperioder = listOf(10.januar til 31.januar)

                val expected =
                    listOf(
                        3.januar til 4.januar,
                        8.januar til 9.januar,
                    )

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("egenmeldinger mellom sykmeldingsperioder") {
                val agp = mockAgp(1.oktober til 16.oktober)
                val sykmeldingsperioder =
                    listOf(
                        2.oktober til 3.oktober,
                        7.oktober til 10.oktober,
                        16.oktober til 24.oktober,
                    )

                val expected =
                    listOf(
                        1.oktober til 1.oktober,
                        4.oktober til 5.oktober,
                        11.oktober til 12.oktober,
                        15.oktober til 15.oktober,
                    )

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("helgedager etter frisk fredag blir egenmeldinger") {
                val agp =
                    mockAgp(
                        1.september til 1.september, // lør
                        9.september til 9.september, // søn
                        15.september til 16.september, // lør-søn
                        22.september til 25.september, // lør-tir
                        30.september til 3.oktober, // søn-ons
                        6.oktober til 6.oktober, // lør
                        7.oktober til 7.oktober, // søn
                        14.oktober til 14.oktober, // søn
                        15.oktober til 15.oktober, // man
                    )
                val sykmeldingsperioder = listOf(16.oktober til 31.oktober)

                val expected =
                    listOf(
                        1.september til 1.september, // lør
                        9.september til 9.september, // søn
                        15.september til 16.september, // lør-søn
                        22.september til 25.september, // lør-tir
                        30.september til 3.oktober, // søn-ons
                        6.oktober til 7.oktober, // lør-søn
                        14.oktober til 15.oktober, // søn-man
                    )

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            test("søndager etter syk fredag og frisk lørdag blir egenmeldinger") {
                val agp =
                    mockAgp(
                        7.september til 7.september, // fre
                        9.september til 9.september, // søn
                        12.september til 14.september, // ons-fre
                        16.september til 19.september, // søn-ons
                        21.september til 21.september, // fre
                        23.september til 23.september, // søn
                        26.september til 28.september, // ons-fre
                        30.september til 1.oktober, // søn-man
                    )
                val sykmeldingsperioder =
                    listOf(
                        21.september til 21.september, // fre
                        26.september til 28.september, // ons-fre
                        2.oktober til 31.oktober,
                    )

                val expected =
                    listOf(
                        7.september til 7.september, // fre
                        9.september til 9.september, // søn
                        12.september til 14.september, // ons-fre
                        16.september til 19.september, // søn-ons
                        23.september til 23.september, // søn
                        30.september til 1.oktober, // søn-man
                    )

                val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }

            context("helgedager etter syk fredag (og lørdag) blir _ikke_ egenmeldinger") {
                val agp =
                    // AGP sjekkes for lengde _før_ egenmeldinger utledes, så vi slipper unna med for lang AGP her
                    mockAgp(
                        4.juni til 19.juni, // 16 dager, fra mandag
                        22.juni til 23.juni, // fre-lør
                        29.juni til 1.juli, // fre-søn
                        6.juli til 10.juli, // fre-tir
                        12.juli til 14.juli, // tor-lør
                        17.juli til 22.juli, // tir-søn
                        24.juli til 1.august, // tir-ons
                        // perioder som slås sammen
                        // fre + lør
                        3.august til 3.august,
                        4.august til 4.august,
                        // fre + lør-søn
                        10.august til 10.august,
                        11.august til 12.august,
                        // fre + lør-tir
                        17.august til 17.august,
                        18.august til 21.august,
                        // tor-fre + lør
                        23.august til 24.august,
                        25.august til 25.august,
                        // ons-fre + lør-søn
                        29.august til 31.august,
                        1.september til 2.september,
                        // ons-fre + lør-ons
                        5.september til 7.september,
                        8.september til 12.september,
                        // fre-lør + søn
                        14.september til 15.september,
                        16.september til 16.september,
                        // tor-lør + søn-tir
                        20.september til 22.september,
                        23.september til 25.september,
                        // fre + lør + søn
                        28.september til 28.september,
                        29.september til 29.september,
                        30.september til 30.september,
                        // tor-fre + lør + søn-tir
                        4.oktober til 5.oktober,
                        6.oktober til 6.oktober,
                        7.oktober til 9.oktober,
                    )

                test("egenmeldt fredag") {
                    val sykmeldingsperioder = listOf(10.oktober til 31.oktober)

                    val expected =
                        listOf(
                            // var 16 dager, fra mandag
                            4.juni til 8.juni,
                            11.juni til 15.juni,
                            18.juni til 19.juni,
                            22.juni til 22.juni, // fre
                            29.juni til 29.juni, // fre
                            // fre + man-tir
                            6.juli til 6.juli,
                            9.juli til 10.juli,
                            12.juli til 13.juli, // tor-fre
                            17.juli til 20.juli, // tir-fre
                            // tir-fre + man-ons
                            24.juli til 27.juli,
                            30.juli til 1.august,
                            // perioder som ble slått sammen
                            // fre
                            3.august til 3.august,
                            // fre
                            10.august til 10.august,
                            // fre + man-tir
                            17.august til 17.august,
                            20.august til 21.august,
                            // tor-fre
                            23.august til 24.august,
                            // ons-fre
                            29.august til 31.august,
                            // ons-fre + man-ons
                            5.september til 7.september,
                            10.september til 12.september,
                            // fre
                            14.september til 14.september,
                            // tor-fre + man-tir
                            20.september til 21.september,
                            24.september til 25.september,
                            // fre
                            28.september til 28.september,
                            // tor-fre + man-tir
                            4.oktober til 5.oktober,
                            8.oktober til 9.oktober,
                        )

                    val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                    actual shouldBe expected
                }

                test("sykmeldt fredag") {
                    val sykmeldingsperioder =
                        listOf(
                            // var 16 dager, fra mandag
                            4.juni til 8.juni,
                            11.juni til 15.juni,
                            18.juni til 19.juni,
                            22.juni til 22.juni, // fre
                            29.juni til 29.juni, // fre
                            // fre + man-tir
                            6.juli til 6.juli,
                            9.juli til 10.juli,
                            12.juli til 13.juli, // tor-fre
                            17.juli til 20.juli, // tir-fre
                            // tir-lør (neste periode man-ons i egenmeldinger)
                            24.juli til 28.juli,
                            // perioder som ble slått sammen
                            // fre
                            3.august til 3.august,
                            // fre
                            10.august til 10.august,
                            // fre + man-tir
                            17.august til 17.august,
                            20.august til 21.august,
                            // tor-fre
                            23.august til 24.august,
                            // ons-fre
                            29.august til 31.august,
                            // ons-fre + man-ons
                            5.september til 7.september,
                            10.september til 12.september,
                            // fre
                            14.september til 14.september,
                            // tor-lør + man-tir
                            20.september til 22.september,
                            24.september til 25.september,
                            // fre
                            28.september til 28.september,
                            // tor-fre (neste periode man-tir i egenmeldinger)
                            4.oktober til 5.oktober,
                            10.oktober til 31.oktober,
                        )

                    val expected =
                        listOf(
                            // man-ons (forrige periode tir-lør i sykmeldinger)
                            30.juli til 1.august,
                            // man-tir (forrige periode tor-fre i sykmeldinger)
                            8.oktober til 9.oktober,
                        )

                    val actual = agp.utledEgenmeldinger(sykmeldingsperioder)

                    actual shouldBe expected
                }
            }

            test("helgegap i egenmeldinger tettes _ikke_") {
                val agp =
                    mockAgp(
                        // 7. er en fredag
                        2.desember til 7.desember,
                        10.desember til 19.desember,
                    )
                val sykmeldingsperioder = listOf(13.desember til 31.desember)

                val expected =
                    listOf(
                        2.desember til 7.desember,
                        10.desember til 12.desember,
                    )

                val actual =
                    agp.utledEgenmeldinger(sykmeldingsperioder)

                actual shouldBe expected
            }
        }
    })

private fun mockAgp(vararg perioder: Periode): Arbeidsgiverperiode =
    Arbeidsgiverperiode(
        perioder = perioder.toList(),
        redusertLoennIAgp = null,
    )

// varierer verdier som resultatet skal være uavhengig av
private suspend fun FunSpecContainerScope.medVarierendeData(
    egenmeldingerFraForespoersel: List<Periode>,
    test: suspend FunSpecContainerScope.(Boolean, List<Periode>) -> Unit,
) {
    withData(
        nameFn = { "erAgpForespurt=${it.first}, egenmeldingerFraForespoersel=${it.second}" },
        true to emptyList(),
        true to egenmeldingerFraForespoersel,
        false to emptyList(),
        false to egenmeldingerFraForespoersel,
    ) { (erAgpForespurt, egenmeldinger) ->
        test(erAgpForespurt, egenmeldinger)
    }
}
