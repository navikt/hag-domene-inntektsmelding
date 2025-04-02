package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.jsonObject
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJson
import no.nav.helsearbeidsgiver.utils.test.date.august
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
class SkjemaInntektsmeldingSelvbestemtTest :
    FunSpec({

        context("innebygget validering") {
            context(SkjemaInntektsmeldingSelvbestemt::sykmeldtFnr.name) {
                test("ugyldig") {
                    val skjemaJson =
                        fulltSkjema()
                            .toJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                            .jsonObject
                            .plus(
                                SkjemaInntektsmeldingSelvbestemt::sykmeldtFnr.name to "123".toJson(),
                            ).toJson()

                    shouldThrowExactly<IllegalArgumentException> {
                        skjemaJson.fromJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                    }
                }
            }

            context(SkjemaInntektsmeldingSelvbestemt::avsender.name) {
                test("ugyldig orgnr") {
                    val skjema = fulltSkjema()

                    val avsenderJson =
                        skjema.avsender
                            .toJson(SkjemaAvsender.serializer())
                            .jsonObject
                            .plus(
                                SkjemaAvsender::orgnr.name to "0".toJson(),
                            ).toJson()

                    val skjemaJson =
                        skjema
                            .toJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                            .jsonObject
                            .plus(
                                SkjemaInntektsmeldingSelvbestemt::avsender.name to avsenderJson,
                            ).toJson()

                    shouldThrowExactly<IllegalArgumentException> {
                        skjemaJson.fromJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                    }
                }
            }

            context(SkjemaInntektsmeldingSelvbestemt::inntekt.name) {
                test("inntekt kan _ikke_ være 'null'") {
                    val skjemaJson =
                        fulltSkjema()
                            .toJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                            .jsonObject
                            .minus(SkjemaInntektsmeldingSelvbestemt::inntekt.name)
                            .toJson()

                    shouldThrowExactly<MissingFieldException> {
                        skjemaJson.fromJson(SkjemaInntektsmeldingSelvbestemt.serializer())
                    }
                }
            }
        }

        context(SkjemaInntektsmeldingSelvbestemt::valider.name) {

            test("skjema uten feil valideres uten feilmeldinger") {
                fulltSkjema().valider().shouldBeEmpty()
            }

            context(SkjemaInntektsmeldingSelvbestemt::avsender.name) {
                test("ugyldig tlf") {
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                avsender =
                                    it.avsender.copy(
                                        tlf = "hæ?",
                                    ),
                            )
                        }

                    skjema.valider() shouldBe setOf(Feilmelding.TLF)
                }
            }

            context(SkjemaInntektsmeldingSelvbestemt::sykmeldingsperioder.name) {
                test("ugyldig tom liste") {
                    val skjema =
                        fulltSkjema().copy(
                            sykmeldingsperioder = emptyList(),
                        )

                    skjema.valider() shouldBe setOf(Feilmelding.SYKEMELDINGER_IKKE_TOM)
                }
            }

            context(SkjemaInntektsmeldingSelvbestemt::agp.name) {
                test("AGP kan være 'null'") {
                    val skjema = fulltSkjema().copy(agp = null)

                    skjema.valider().shouldBeEmpty()
                }

                test("AGP kan _ikke_ være tom når AG betaler full lønn i AGP") {
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                agp =
                                    it.agp?.copy(
                                        perioder = emptyList(),
                                        redusertLoennIAgp = null,
                                    ),
                            )
                        }

                    skjema.valider() shouldBe setOf(Feilmelding.AGP_IKKE_TOM)
                }

                test("AGP kan være tom når AG _ikke_ betaler full lønn i AGP") {
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                agp =
                                    it.agp?.copy(
                                        perioder = emptyList(),
                                        redusertLoennIAgp =
                                            RedusertLoennIAgp(
                                                beloep = 22000.0,
                                                begrunnelse = RedusertLoennIAgp.Begrunnelse.FiskerMedHyre,
                                            ),
                                    ),
                            )
                        }

                    skjema.valider().shouldBeEmpty()
                }

                test("AGP kan være maks 16 dager") {
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                agp =
                                    it.agp?.copy(
                                        perioder =
                                            listOf(
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
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                agp =
                                    it.agp?.copy(
                                        egenmeldinger = emptyList(),
                                    ),
                            )
                        }

                    skjema.valider().shouldBeEmpty()
                }

                context(Arbeidsgiverperiode::redusertLoennIAgp.name) {
                    test("'redusertLoennIAgp' kan være 'null'") {
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    agp =
                                        it.agp?.copy(
                                            redusertLoennIAgp = null,
                                        ),
                                )
                            }

                        skjema.valider().shouldBeEmpty()
                    }

                    testBeloep { beloep, forventetFeil ->
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    agp =
                                        it.agp?.copy(
                                            redusertLoennIAgp =
                                                RedusertLoennIAgp(
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

            context(SkjemaInntektsmeldingSelvbestemt::inntekt.name) {

                context(Inntekt::beloep.name) {
                    testBeloep { beloep, forventetFeil ->
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    inntekt =
                                        it.inntekt.copy(
                                            beloep = beloep,
                                        ),
                                )
                            }

                        skjema.valider() shouldContainAll forventetFeil
                    }
                }

                context(Inntekt::naturalytelser.name) {
                    test("'naturalytelser' kan være tom") {
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    inntekt =
                                        it.inntekt.copy(
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
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    inntekt =
                                        it.inntekt.copy(
                                            naturalytelser =
                                                listOf(
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
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                inntekt =
                                    it.inntekt.copy(
                                        endringAarsak = null,
                                    ),
                            )
                        }

                    skjema.valider().shouldBeEmpty()
                }
            }

            context(SkjemaInntektsmeldingSelvbestemt::refusjon.name) {
                test("refusjon kan være 'null'") {
                    val skjema = fulltSkjema().copy(refusjon = null)

                    skjema.valider().shouldBeEmpty()
                }

                context(Refusjon::beloepPerMaaned.name) {
                    testBeloep { beloep, forventetFeil ->
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    refusjon =
                                        it.refusjon?.copy(
                                            beloepPerMaaned = beloep,
                                        ),
                                )
                            }

                        skjema.valider() shouldContainAll forventetFeil
                    }
                }

                context(Refusjon::endringer.name) {
                    test("'endringer' kan være tom") {
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    refusjon =
                                        it.refusjon?.copy(
                                            endringer = emptyList(),
                                        ),
                                )
                            }

                        skjema.valider().shouldBeEmpty()
                    }

                    testBeloep { beloep, forventetFeil ->
                        val skjema =
                            fulltSkjema().let {
                                it.copy(
                                    refusjon =
                                        it.refusjon?.copy(
                                            endringer =
                                                listOf(
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
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                refusjon =
                                    it.refusjon?.copy(
                                        sluttdato = null,
                                    ),
                            )
                        }

                    skjema.valider().shouldBeEmpty()
                }

                test("ugyldig dato i endring (må være før eller lik (non-null) 'sluttdato')") {
                    val skjema =
                        fulltSkjema().let {
                            it.copy(
                                refusjon =
                                    it.refusjon?.copy(
                                        endringer =
                                            listOf(
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

            test("bestemmende fraværsdag før inntektsdato") {
                val skjema =
                    fulltSkjema().let {
                        it.copy(
                            agp =
                                it.agp?.copy(
                                    perioder =
                                        listOf(
                                            6.juni til 21.juni,
                                        ),
                                ),
                            inntekt =
                                it.inntekt.copy(
                                    inntektsdato = 7.juni,
                                ),
                        )
                    }

                skjema.valider() shouldBe setOf(Feilmelding.TEKNISK_FEIL)
            }

            test("refusjonsbeløp over inntekt") {
                val skjema =
                    fulltSkjema().let {
                        it.copy(
                            inntekt =
                                it.inntekt.copy(
                                    beloep = 15000.0,
                                ),
                            refusjon =
                                it.refusjon?.copy(
                                    beloepPerMaaned = 15001.0,
                                ),
                        )
                    }

                skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_OVER_INNTEKT)
            }

            test("refusjonsbeløp i endring over inntekt") {
                val skjema =
                    fulltSkjema().let {
                        it.copy(
                            inntekt =
                                it.inntekt.copy(
                                    beloep = 8000.0,
                                ),
                            refusjon =
                                it.refusjon?.copy(
                                    endringer =
                                        listOf(
                                            RefusjonEndring(
                                                beloep = 8000.1,
                                                startdato = 19.juni,
                                            ),
                                        ),
                                ),
                        )
                    }

                skjema.valider() shouldBe setOf(Feilmelding.REFUSJON_OVER_INNTEKT)
            }

            test("duplikate feilmeldinger fjernes") {
                val skjema =
                    fulltSkjema().let {
                        it.copy(
                            agp =
                                it.agp?.copy(
                                    redusertLoennIAgp =
                                        it.agp?.redusertLoennIAgp?.copy(
                                            beloep = -11.0,
                                        ),
                                ),
                            refusjon =
                                it.refusjon?.copy(
                                    beloepPerMaaned = -22.0,
                                ),
                        )
                    }

                skjema.valider() shouldBe setOf(Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL)
            }

            test("ulike feilmeldinger bevares") {
                val skjema =
                    fulltSkjema().let {
                        it.copy(
                            avsender =
                                it.avsender.copy(
                                    tlf = "112",
                                ),
                            sykmeldingsperioder = emptyList(),
                            refusjon =
                                it.refusjon?.copy(
                                    beloepPerMaaned = -17.0,
                                ),
                        )
                    }

                skjema.valider() shouldBe
                    setOf(
                        Feilmelding.TLF,
                        Feilmelding.SYKEMELDINGER_IKKE_TOM,
                        Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL,
                    )
            }
        }
    })

private fun fulltSkjema(): SkjemaInntektsmeldingSelvbestemt =
    SkjemaInntektsmeldingSelvbestemt(
        selvbestemtId = UUID.randomUUID(),
        sykmeldtFnr = Fnr("11037400132"),
        avsender =
            SkjemaAvsender(
                orgnr = Orgnr("888414223"),
                tlf = "45456060",
            ),
        sykmeldingsperioder =
            listOf(
                5.juni til 20.juni,
                21.juni til 30.juni,
                6.juli til 25.juli,
            ),
        agp =
            Arbeidsgiverperiode(
                perioder =
                    listOf(
                        2.juni til 2.juni,
                        4.juni til 18.juni,
                    ),
                egenmeldinger =
                    listOf(
                        2.juni til 2.juni,
                        4.juni til 5.juni,
                    ),
                redusertLoennIAgp =
                    RedusertLoennIAgp(
                        beloep = 34000.0,
                        begrunnelse = RedusertLoennIAgp.Begrunnelse.LovligFravaer,
                    ),
            ),
        inntekt =
            Inntekt(
                beloep = 50000.0,
                inntektsdato = 31.mai,
                naturalytelser =
                    listOf(
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
                endringAarsak =
                    Tariffendring(
                        gjelderFra = 30.juni,
                        bleKjent = 5.juli,
                    ),
                endringAarsaker =
                    listOf(
                        Tariffendring(
                            gjelderFra = 30.juni,
                            bleKjent = 5.juli,
                        ),
                    ),
            ),
        refusjon =
            Refusjon(
                beloepPerMaaned = 10000.0,
                endringer =
                    listOf(
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
        vedtaksperiodeId = UUID.randomUUID(),
    )
