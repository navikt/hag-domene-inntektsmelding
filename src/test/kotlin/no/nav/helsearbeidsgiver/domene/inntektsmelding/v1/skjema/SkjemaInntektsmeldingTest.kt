package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai

class SkjemaInntektsmeldingTest : FunSpec({

    context(SkjemaInntektsmelding::valider.name) {

        test("skjema uten feil valideres uten feilmeldinger") {
            fulltSkjema().valider().shouldBeEmpty()
        }

        context(SkjemaInntektsmelding::sykmeldtFnr.name) {
            test("ugyldig") {
                val skjema = fulltSkjema().copy(
                    sykmeldtFnr = "123",
                )

                skjema.valider() shouldBe setOf(Feilmelding.FNR)
            }
        }

        context(SkjemaInntektsmelding::avsender.name) {
            test("ugyldig orgnr") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        avsender = it.avsender.copy(
                            orgnr = "0",
                        ),
                    )
                }

                skjema.valider() shouldBe setOf(Feilmelding.ORGNR)
            }

            test("ugyldig tlf") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        avsender = it.avsender.copy(
                            tlf = "hæ?",
                        ),
                    )
                }

                skjema.valider() shouldBe setOf(Feilmelding.TLF)
            }
        }

        context(SkjemaInntektsmelding::sykmeldingsperioder.name) {
            test("ugyldig tom liste") {
                val skjema = fulltSkjema().copy(
                    sykmeldingsperioder = emptyList(),
                )

                skjema.valider() shouldBe setOf(Feilmelding.SYKEMELDINGER_IKKE_TOM)
            }
        }

        context(SkjemaInntektsmelding::agp.name) {
            test("AGP kan være 'null'") {
                val skjema = fulltSkjema().copy(agp = null)

                skjema.valider().shouldBeEmpty()
            }

            test("AGP kan _ikke_ være tom når AG betaler full lønn i AGP") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        agp = it.agp?.copy(
                            perioder = emptyList(),
                            redusertLoennIAgp = null,
                        ),
                    )
                }

                skjema.valider() shouldBe setOf(Feilmelding.AGP_IKKE_TOM)
            }

            test("AGP kan være tom når AG _ikke_ betaler full lønn i AGP") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        agp = it.agp?.copy(
                            perioder = emptyList(),
                            redusertLoennIAgp = RedusertLoennIAgp(
                                beloep = 22000.0,
                                begrunnelse = RedusertLoennIAgp.Begrunnelse.FiskerMedHyre,
                            ),
                        ),
                    )
                }

                skjema.valider().shouldBeEmpty()
            }

            test("egenmeldinger kan være tom") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        agp = it.agp?.copy(
                            egenmeldinger = emptyList(),
                        ),
                    )
                }

                skjema.valider().shouldBeEmpty()
            }

            context(Arbeidsgiverperiode::redusertLoennIAgp.name) {
                test("'redusertLoennIAgp' kan være 'null'") {
                    val skjema = fulltSkjema().let {
                        it.copy(
                            agp = it.agp?.copy(
                                redusertLoennIAgp = null,
                            ),
                        )
                    }

                    skjema.valider().shouldBeEmpty()
                }

                testBeloep { beloep, forventetFeil ->
                    val skjema = fulltSkjema().let {
                        it.copy(
                            agp = it.agp?.copy(
                                redusertLoennIAgp = RedusertLoennIAgp(
                                    beloep = beloep,
                                    begrunnelse = RedusertLoennIAgp.Begrunnelse.Saerregler,
                                ),
                            ),
                        )
                    }

                    skjema.valider() shouldContainAll forventetFeil
                }
            }
        }

        context(SkjemaInntektsmelding::inntekt.name) {
            test("inntekt kan være 'null'") {
                val skjema = fulltSkjema().copy(inntekt = null)

                skjema.valider().shouldBeEmpty()
            }

            context(Inntekt::beloep.name) {
                testBeloep { beloep, forventetFeil ->
                    val skjema = fulltSkjema().let {
                        it.copy(
                            inntekt = it.inntekt?.copy(
                                beloep = beloep,
                            ),
                        )
                    }

                    skjema.valider() shouldContainAll forventetFeil
                }
            }

            context(Inntekt::naturalytelser.name) {
                test("'naturalytelser' kan være tom") {
                    val skjema = fulltSkjema().let {
                        it.copy(
                            inntekt = it.inntekt?.copy(
                                naturalytelser = emptyList(),
                            ),
                        )
                    }

                    skjema.valider().shouldBeEmpty()
                }

                withData(
                    nameFn = { (beloep, forventetFeil) ->
                        "beløp $beloep gir ${forventetFeil.size} feil"
                    },
                    1.0 to emptySet(),
                    0.0 to setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL),
                    -1.0 to setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL),
                    1_000_000.0 to setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL),
                ) { (beloep, forventetFeil) ->
                    val skjema = fulltSkjema().let {
                        it.copy(
                            inntekt = it.inntekt?.copy(
                                naturalytelser = listOf(
                                    Naturalytelse(
                                        naturalytelse = Naturalytelse.Kode.BIL,
                                        verdiBeloep = beloep,
                                        sluttdato = 20.juni,
                                    ),
                                ),
                            ),
                        )
                    }

                    skjema.valider() shouldContainAll forventetFeil
                }
            }

            test("'endringAarsak' kan være 'null'") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        inntekt = it.inntekt?.copy(
                            endringAarsak = null,
                        ),
                    )
                }

                skjema.valider().shouldBeEmpty()
            }
        }

        context(SkjemaInntektsmelding::refusjon.name) {
            test("refusjon kan være 'null'") {
                val skjema = fulltSkjema().copy(refusjon = null)

                skjema.valider().shouldBeEmpty()
            }

            context(Refusjon::beloepPerMaaned.name) {
                testBeloep { beloep, forventetFeil ->
                    val skjema = fulltSkjema().let {
                        it.copy(
                            refusjon = it.refusjon?.copy(
                                beloepPerMaaned = beloep,
                            ),
                        )
                    }

                    skjema.valider() shouldContainAll forventetFeil
                }
            }

            context(Refusjon::endringer.name) {
                test("'endringer' kan være tom") {
                    val skjema = fulltSkjema().let {
                        it.copy(
                            refusjon = it.refusjon?.copy(
                                endringer = emptyList(),
                            ),
                        )
                    }

                    skjema.valider().shouldBeEmpty()
                }

                testBeloep { beloep, forventetFeil ->
                    val skjema = fulltSkjema().let {
                        it.copy(
                            refusjon = it.refusjon?.copy(
                                endringer = listOf(
                                    RefusjonEndring(
                                        beloep = beloep,
                                        startdato = 21.juni,
                                    ),
                                ),
                            ),
                        )
                    }

                    skjema.valider() shouldContainAll forventetFeil
                }
            }

            test("'sluttdato' kan være 'null'") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        refusjon = it.refusjon?.copy(
                            sluttdato = null,
                        ),
                    )
                }

                skjema.valider().shouldBeEmpty()
            }

            test("ugyldig dato i endring (må være før eller lik (non-null) 'sluttdato')") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        refusjon = it.refusjon?.copy(
                            endringer = listOf(
                                RefusjonEndring(
                                    beloep = 4567.0,
                                    startdato = 4.august,
                                ),
                            ),
                            sluttdato = 1.august,
                        ),
                    )
                }

                skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_ENDRING_DATO)
            }
        }

        test("refusjonsbeløp over inntekt") {
            val skjema = fulltSkjema().let {
                it.copy(
                    inntekt = it.inntekt?.copy(
                        beloep = 15000.0,
                    ),
                    refusjon = it.refusjon?.copy(
                        beloepPerMaaned = 15001.0,
                    ),
                )
            }

            skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_OVER_INNTEKT)
        }

        test("refusjonsbeløp i endring over inntekt") {
            val skjema = fulltSkjema().let {
                it.copy(
                    inntekt = it.inntekt?.copy(
                        beloep = 8000.0,
                    ),
                    refusjon = it.refusjon?.copy(
                        endringer = listOf(
                            RefusjonEndring(
                                beloep = 8000.1,
                                startdato = 8.juni,
                            ),
                        ),
                    ),
                )
            }

            skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_OVER_INNTEKT)
        }

        test("duplikate feilmeldinger fjernes") {
            val skjema = fulltSkjema().let {
                it.copy(
                    agp = it.agp?.copy(
                        redusertLoennIAgp = it.agp?.redusertLoennIAgp?.copy(
                            beloep = -11.0,
                        ),
                    ),
                    refusjon = it.refusjon?.copy(
                        beloepPerMaaned = -22.0,
                    ),
                )
            }

            skjema.valider() shouldBe setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL)
        }

        test("ulike feilmeldinger bevares") {
            val skjema = fulltSkjema().let {
                it.copy(
                    sykmeldtFnr = "120199",
                    sykmeldingsperioder = emptyList(),
                    refusjon = it.refusjon?.copy(
                        beloepPerMaaned = -17.0,
                    ),
                )
            }

            skjema.valider() shouldBe setOf(
                Feilmelding.FNR,
                Feilmelding.SYKEMELDINGER_IKKE_TOM,
                Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL,
            )
        }
    }
})

private suspend fun ContainerScope.testBeloep(
    testFn: (Double, Set<String>) -> Unit,
) {
    withData(
        nameFn = { (beloep, forventetFeil) ->
            "beløp $beloep gir ${forventetFeil.size} feil"
        },
        0.0 to emptySet(),
        10000.0 to emptySet(),
        -1.0 to setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL),
        1_000_000.0 to setOf(Feilmelding.BELOEP_STOERRE_ELLER_LIK_NULL),
    ) { (beloep, forventetFeil) ->
        testFn(beloep, forventetFeil)
    }
}

private fun fulltSkjema(): SkjemaInntektsmelding =
    SkjemaInntektsmelding(
        sykmeldtFnr = "11037400132",
        avsender = SkjemaAvsender(
            orgnr = "888414223",
            tlf = "45456060",
        ),
        sykmeldingsperioder = listOf(
            5.juni til 20.juni,
            21.juni til 30.juni,
            6.juli til 25.juli,
        ),
        agp = Arbeidsgiverperiode(
            perioder = listOf(
                2.juni til 2.juni,
                4.juni til 18.juni,
            ),
            egenmeldinger = listOf(
                2.juni til 2.juni,
                4.juni til 5.juni,
            ),
            redusertLoennIAgp = RedusertLoennIAgp(
                beloep = 34000.0,
                begrunnelse = RedusertLoennIAgp.Begrunnelse.LovligFravaer,
            ),
        ),
        inntekt = Inntekt(
            beloep = 50000.0,
            inntektsdato = 31.mai,
            naturalytelser = listOf(
                Naturalytelse(
                    naturalytelse = Naturalytelse.Kode.OPSJONER,
                    verdiBeloep = 4000.0,
                    sluttdato = 15.juni,
                ),
                Naturalytelse(
                    naturalytelse = Naturalytelse.Kode.ELEKTRONISKKOMMUNIKASJON,
                    verdiBeloep = 555.0,
                    sluttdato = 25.juni,
                ),
            ),
            endringAarsak = Tariffendring(
                gjelderFra = 30.juni,
                bleKjent = 5.juli,
            ),
        ),
        refusjon = Refusjon(
            beloepPerMaaned = 10000.0,
            endringer = listOf(
                RefusjonEndring(
                    beloep = 8000.0,
                    startdato = 10.juni,
                ),
                RefusjonEndring(
                    beloep = 6000.0,
                    startdato = 20.juni,
                ),
            ),
            sluttdato = 30.juni,
        ),
        aarsakInnsending = AarsakInnsending.Endring,
    )
