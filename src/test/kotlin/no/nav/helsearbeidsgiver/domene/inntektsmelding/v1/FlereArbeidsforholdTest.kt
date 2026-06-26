package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding

class FlereArbeidsforholdTest :
    FunSpec({

        test("flere arbeidsforhold uten feil valideres uten feilmeldinger") {
            TestData.flereArbeidsforhold.valider().shouldBeEmpty()
        }

        test("Kan ikke ha lik lønn i alle arbeidsforhold") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    harLikLoenn = true,
                )

            flere.valider() shouldContainExactly listOf(FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_ULIK_LOENN))
        }

        test("Kan ikke være syk fra alle") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    erSykmeldtFraAlle = true,
                )

            flere.valider() shouldContainExactly listOf(FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_IKKE_SYK_FRA_ALLE))
        }

        test("Må ha minst to arbeidsforhold") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    arbeidsforhold = listOf(TestData.lagArbeidsforhold().copy(inkludertISykefravaer = true)),
                )
            flere.valider() shouldContainExactly
                listOf(
                    FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_MINST_TO),
                    FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_IKKE_ALLE_INKLUDERT),
                )
        }

        context("Minst ett arbeidsforhold skal være inkludert i sykepengegrunnlag, ikke null og ikke alle") {
            withData(
                nameFn = { (inkluderte, forventetFeil) ->
                    "$inkluderte gir feil $forventetFeil"
                },
                listOf(true, false, false) to emptySet(),
                listOf(false, true, false) to emptySet(),
                listOf(true, true, false) to emptySet(),
                listOf(true, true, true) to setOf(FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_IKKE_ALLE_INKLUDERT)),
                listOf(false, false, false) to setOf(FeiletValidering(Feilmelding.FLERE_ARBEIDSFORHOLD_MINST_ETT_INKLUDERT)),
            ) { (inkluderte, forventetFeil) ->
                val flereArbeidsforhold =
                    TestData.flereArbeidsforhold.copy(
                        arbeidsforhold = inkluderte.map { TestData.lagArbeidsforhold().copy(inkludertISykefravaer = it) },
                    )

                flereArbeidsforhold.valider() shouldBe forventetFeil
            }
        }
    })
