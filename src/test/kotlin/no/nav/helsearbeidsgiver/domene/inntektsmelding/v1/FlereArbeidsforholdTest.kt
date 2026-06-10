package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai

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

            flere.valider() shouldContainExactly listOf(FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_MED_LIK_LOENN))
        }

        test("Kan ikke være syk fra alle") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    erSykmeldtFraAlle = true,
                )

            flere.valider() shouldContainExactly listOf(FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_SYK_FRA_ALLE))
        }

        test("Må ha minst én dato med flere arbeidsforhold") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    arbeidsforholdPerFom = emptyMap(),
                )

            flere.valider() shouldContainExactly listOf(FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_IKKE_TOM))
        }

        test("Må ha minst to arbeidsforhold") {
            val flere =
                TestData.flereArbeidsforhold.copy(
                    arbeidsforholdPerFom =
                        mapOf(
                            11.mai to listOf(TestData.lagArbeidsforhold().copy(inkludertISykefravaer = true)),
                        ),
                )
            flere.valider() shouldContainExactly
                listOf(
                    FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_FOM_MINST_TO),
                    FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_FOM_ALLE_ARBEIDSFORHOLD),
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
                listOf(true, true, true) to setOf(FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_FOM_ALLE_ARBEIDSFORHOLD)),
                listOf(false, false, false) to setOf(FeiletValidering(Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_PER_FOM_INGEN_ARBEIDSFORHOLD)),
            ) { (inkluderte, forventetFeil) ->
                val arbeidsforhold = inkluderte.map { TestData.lagArbeidsforhold().copy(inkludertISykefravaer = it) }.toList()

                val flereArbeidsforhold =
                    TestData.flereArbeidsforhold.copy(
                        arbeidsforholdPerFom = mapOf(20.juni to arbeidsforhold),
                    )

                flereArbeidsforhold.valider() shouldBe forventetFeil
            }
        }
    })
