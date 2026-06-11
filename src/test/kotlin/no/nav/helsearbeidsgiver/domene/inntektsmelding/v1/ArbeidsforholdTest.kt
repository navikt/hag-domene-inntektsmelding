package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.TestData.lagArbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.KREVER_BELOEP_STOERRE_ELLER_LIK_NULL
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_ARBEIDSFORHOLD_STILLINGSPROSENT
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_ARBEIDSFORHOLD_YRKESBESKRIVELSE
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.MAKS_GRENSE_BELOEP

class ArbeidsforholdTest :
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
                -1.0 to setOf(FeiletValidering(UGYLDIG_ARBEIDSFORHOLD_STILLINGSPROSENT)),
                100.1 to setOf(FeiletValidering(UGYLDIG_ARBEIDSFORHOLD_STILLINGSPROSENT)),
            ) { (stillingsprosent, forventetFeil) ->

                val arbeidsforhold =

                    Arbeidsforhold(
                        inkludertISykefravaer = true,
                        yrkesbeskrivelse = "Jordmor",
                        stillingsprosent = stillingsprosent,
                        inntekt = 1.0,
                    )
                arbeidsforhold.valider() shouldBe forventetFeil
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

                val arbeidsforhold =

                    Arbeidsforhold(
                        inkludertISykefravaer = true,
                        yrkesbeskrivelse = "Jordmor",
                        stillingsprosent = 10.0,
                        inntekt = inntekt,
                    )

                arbeidsforhold.valider() shouldBe forventetFeil
            }
        }

        context("Validerer at yrkesbeskrivelse kun består av latinske tegn og gyldige spesialtegn") {
            withData(
                nameFn = { (yrkesbeskrivelse, forventetFeil) ->
                    "$yrkesbeskrivelse gir feil $forventetFeil"
                },
                "Snekker (lærling)" to emptySet(),
                "select * from inntektsmelding;" to setOf(FeiletValidering(UGYLDIG_ARBEIDSFORHOLD_YRKESBESKRIVELSE)),
            ) { (yrkesbeskrivelse, forventetFeil) ->
                val arbeidsforhold = lagArbeidsforhold().copy(inkludertISykefravaer = true, yrkesbeskrivelse = yrkesbeskrivelse)
                arbeidsforhold.valider() shouldBe forventetFeil
            }
        }
    })
