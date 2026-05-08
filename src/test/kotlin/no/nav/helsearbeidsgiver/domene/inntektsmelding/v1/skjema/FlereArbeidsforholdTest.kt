package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_STILLINGSPROSENT

class FlereArbeidsforholdTest :
    FunSpec({

        context("arbeidsforhold med korrekt stillingsprosent") {
            // negativ eller større enn 100 er ikke lov når man rapporterer inn flere arbeidsforhold..
            withData(
                nameFn = { (stillingsprosent, forventetFeil) ->
                    "stillingsprosent $stillingsprosent gir feil $forventetFeil"
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
    })
