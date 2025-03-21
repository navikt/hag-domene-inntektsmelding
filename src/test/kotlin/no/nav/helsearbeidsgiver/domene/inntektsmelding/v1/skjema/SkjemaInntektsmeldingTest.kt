package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
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
import java.util.UUID

class SkjemaInntektsmeldingTest : FunSpec({

    context(SkjemaInntektsmelding::valider.name) {

        test("skjema uten feil valideres uten feilmeldinger") {
            fulltSkjema().valider().shouldBeEmpty()
        }

        context(SkjemaInntektsmelding::avsenderTlf.name) {
            test("ugyldig tlf") {
                val skjema = fulltSkjema().copy(
                    avsenderTlf = "hæ?",
                )

                skjema.valider() shouldBe setOf(Feilmelding.TLF)
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

            test("AGP kan være maks 16 dager") {
                val skjema = fulltSkjema().let {
                    it.copy(
                        agp = it.agp?.copy(
                            perioder = listOf(
                                8.august til 17.august,
                                20.august til 31.august,
                            ),
                        ),
                        refusjon = null,
                    )
                }

                skjema.valider() shouldBe setOf(Feilmelding.AGP_MAKS_16)
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
                    0.0 to setOf(Feilmelding.KREVER_BELOEP_STOERRE_ENN_NULL),
                    -1.0 to setOf(Feilmelding.KREVER_BELOEP_STOERRE_ENN_NULL),
                    1_000_000.0 to setOf(Feilmelding.KREVER_BELOEP_STOERRE_ENN_NULL),
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

            test("dato for refusjonEndring må være etter AGP") {
                val agpFom = 4.juni
                val agpTom = 18.juni

                val agp = Arbeidsgiverperiode(listOf(Periode(fom = agpFom, tom = agpTom)), emptyList(), null)
                val ugyldigRefusjon = Refusjon(
                    beloepPerMaaned = 50000.0,
                    endringer = listOf(RefusjonEndring(beloep = 10.0, startdato = agpTom)),
                    sluttdato = null,
                )
                val skjema = fulltSkjema().copy(
                    agp = agp,
                    refusjon = ugyldigRefusjon,
                )

                skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_ENDRING_FOER_AGP_SLUTT)
            }

            test("dato for refusjonEndring må være etter inntektDato hvis ingen AGP") {
                val inntektDato = 1.juni

                val inntekt = Inntekt(
                    beloep = 51000.0,
                    inntektsdato = inntektDato,
                    naturalytelser = emptyList(),
                    endringAarsak = null,
                    endringAarsaker = null,
                )
                val ugyldigRefusjon = Refusjon(
                    beloepPerMaaned = 50000.0,
                    endringer = listOf(RefusjonEndring(beloep = 10.0, startdato = inntektDato.minusDays(1))),
                    sluttdato = null,
                )
                val skjema = fulltSkjema().copy(
                    inntekt = inntekt,
                    agp = null,
                    refusjon = ugyldigRefusjon,
                )

                skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_ENDRING_FOER_INNTEKTDATO)
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
                                startdato = 8.juli,
                            ),
                        ),
                    ),
                )
            }

            skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_OVER_INNTEKT)
        }

        test("for mange inntekt endringsaarsaker") {
            val skjema = fulltSkjema().let {
                it.copy(
                    inntekt = it.inntekt?.copy(
                        endringAarsaker = List(50) { Bonus },
                    ),
                )
            }

            skjema.valider() shouldBe setOf(Feilmelding.FOR_MANGE_INNTEKT_ENDRINGSAARSAKER)
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

            skjema.valider() shouldBe setOf(Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL)
        }

        test("ulike feilmeldinger bevares") {
            val skjema = fulltSkjema().let {
                it.copy(
                    avsenderTlf = "112",
                    inntekt = it.inntekt?.copy(
                        naturalytelser = listOf(
                            Naturalytelse(
                                naturalytelse = Naturalytelse.Kode.OPSJONER,
                                verdiBeloep = 0.0,
                                sluttdato = 15.juni,
                            ),
                        ),
                    ),
                    refusjon = it.refusjon?.copy(
                        beloepPerMaaned = -17.0,
                    ),
                )
            }

            skjema.valider() shouldBe setOf(
                Feilmelding.TLF,
                Feilmelding.KREVER_BELOEP_STOERRE_ENN_NULL,
                Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
            )
        }
    }
})

internal suspend fun ContainerScope.testBeloep(
    testFn: (Double, Set<String>) -> Unit,
) {
    withData(
        nameFn = { (beloep, forventetFeil) ->
            "beløp $beloep gir ${forventetFeil.size} feil"
        },
        0.0 to emptySet(),
        10000.0 to emptySet(),
        -1.0 to setOf(Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL),
        1_000_000.0 to setOf(Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL),
    ) { (beloep, forventetFeil) ->
        testFn(beloep, forventetFeil)
    }
}

private fun fulltSkjema(): SkjemaInntektsmelding =
    SkjemaInntektsmelding(
        forespoerselId = UUID.randomUUID(),
        avsenderTlf = "45456060",
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
            endringAarsaker = listOf(
                Tariffendring(
                    gjelderFra = 30.juni,
                    bleKjent = 5.juli,
                ),
            ),
        ),
        refusjon = Refusjon(
            beloepPerMaaned = 10000.0,
            endringer = listOf(
                RefusjonEndring(
                    beloep = 8000.0,
                    startdato = 10.juli,
                ),
                RefusjonEndring(
                    beloep = 6000.0,
                    startdato = 20.juli,
                ),
            ),
            sluttdato = null,
        ),
    )
