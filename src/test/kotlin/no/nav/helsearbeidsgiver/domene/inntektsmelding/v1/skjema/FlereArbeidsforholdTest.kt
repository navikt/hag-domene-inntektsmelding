package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.TestData.lagArbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_YRKESBESKRIVELSE
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.MAKS_GRENSE_BELOEP

class FlereArbeidsforholdTest :
    FunSpec({

        context("arbeidsforhold med korrekt stillingsprosent") {
            // negativ eller større enn 100 er ikke lov når man rapporterer inn flere arbeidsforhold..
            withData(
                nameFn = { (stillingsprosent, forventetFeil) ->
                    "stillingsprosent $stillingsprosent gir $forventetFeil"
                },
                1.0 to emptySet(),
                100.0 to emptySet(),
                0.0 to emptySet(), // 0 % gir nok mening bare dersom arbeidsforholdet ikke skal telle
                -1.0 to setOf(FeiletValidering(UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT)),
                100.1 to setOf(FeiletValidering(UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT)),
            ) { (stillingsprosent, forventetFeil) ->

                val ugyldig =
                    FlereArbeidsforhold(
                        harLikLoenn = false,
                        erSykmeldtFraAlle = false,
                        arbeidsforhold =
                            listOf(
                                Arbeidsforhold(
                                    inkludertISykefravaer = true,
                                    yrkesbeskrivelse = "Jordmor",
                                    stillingsprosent = stillingsprosent,
                                    inntekt = 1.0,
                                ),
                                Arbeidsforhold(
                                    inkludertISykefravaer = false,
                                    yrkesbeskrivelse = "Maler",
                                    stillingsprosent = 5.0,
                                    inntekt = 1.0,
                                ),
                            ),
                    )
                ugyldig.valider() shouldBe forventetFeil
            }
        }

        context("arbeidsforhold med korrekt inntekt") {
            // negativ eller større enn maxbeløp er ikke lov
            withData(
                nameFn = { (inntekt, forventetFeil) ->
                    "inntekt $inntekt gir feil $forventetFeil"
                },
                1.0 to emptySet(),
                100.0 to emptySet(),
                0.0 to emptySet(), // 0 i inntekt gir nok mening bare dersom arbeidsforholdet ikke skal telle
                -1.0 to setOf(FeiletValidering(KREVER_BELOEP_STOERRE_ELLER_LIK_NULL)),
                MAKS_GRENSE_BELOEP to setOf(FeiletValidering(KREVER_BELOEP_STOERRE_ELLER_LIK_NULL)),
            ) { (inntekt, forventetFeil) ->

                val ugyldig =
                    FlereArbeidsforhold(
                        harLikLoenn = false,
                        erSykmeldtFraAlle = false,
                        arbeidsforhold =
                            listOf(
                                Arbeidsforhold(
                                    inkludertISykefravaer = true,
                                    yrkesbeskrivelse = "Jordmor",
                                    stillingsprosent = 10.0,
                                    inntekt = inntekt,
                                ),
                                Arbeidsforhold(
                                    inkludertISykefravaer = false,
                                    yrkesbeskrivelse = "Maler",
                                    stillingsprosent = 5.0,
                                    inntekt = 1.0,
                                ),
                            ),
                    )
                ugyldig.valider() shouldBe forventetFeil
            }
        }

        context("Minst ett arbeidsforhold skal være inkludert i sykepengegrunnlag, ikke null og ikke alle") {
            withData(
                nameFn = { (inkluderte, forventetFeil) ->
                    "$inkluderte gir feil $forventetFeil"
                },
                listOf(true, false, false) to emptySet(),
                listOf(false, true, false) to emptySet(),
                listOf(true, true, false) to emptySet(),
                listOf(true, true, true) to setOf(FeiletValidering(UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD)),
                listOf(false, false, false) to setOf(FeiletValidering(UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD)),
            ) { (inkluderte, forventetFeil) ->
                val arbeidsforhold = inkluderte.map { lagArbeidsforhold(it) }.toList()
                val flereArbeidsforhold = FlereArbeidsforhold(false, false, arbeidsforhold)
                flereArbeidsforhold.valider() shouldBe forventetFeil
            }
        }

        context("Validerer at yrkesbeskrivelse kun består av latinske tegn og gyldige spesialtegn") {
            withData(
                nameFn = { (yrkesbeskrivelse, forventetFeil) ->
                    "$yrkesbeskrivelse gir feil $forventetFeil"
                },
                "Snekker (lærling)" to emptySet(),
                "select * from inntektsmelding;" to setOf(FeiletValidering(UGYLDIG_FLERE_ARBEIDSFORHOLD_YRKESBESKRIVELSE)),
            ) { (yrkesbeskrivelse, forventetFeil) ->
                val arbeidsforhold1 = lagArbeidsforhold(inkludertISykefravaer = true, yrkesbeskrivelse = yrkesbeskrivelse)
                val arbeidsforhold2 = lagArbeidsforhold(inkludertISykefravaer = false, yrkesbeskrivelse = "Jordmor")
                val flereArbeidsforhold = FlereArbeidsforhold(false, false, listOf(arbeidsforhold1, arbeidsforhold2))
                flereArbeidsforhold.valider() shouldBe forventetFeil
            }
        }
    })
