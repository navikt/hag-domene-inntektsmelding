package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.TestData.lagArbeidsforhold
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_ALLE_ARBEIDSFORHOLD
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding.UGYLDIG_FLERE_ARBEIDSFORHOLD_INGEN_ARBEIDSFORHOLD

class FlereArbeidsforholdTest :
    FunSpec({

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
    })
