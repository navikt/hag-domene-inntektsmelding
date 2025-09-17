package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api.AvsenderSystem
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.util.UUID

class InntektsmeldingTypeTest :
    FunSpec({

        context("Inntektsmelding.Type") {

            withData(
                nameFn = { "${it::class.simpleName} har Nav som avsendersystem" },
                Inntektsmelding.Type.Forespurt(UUID.randomUUID()),
                Inntektsmelding.Type.Selvbestemt(UUID.randomUUID()),
                Inntektsmelding.Type.Fisker(UUID.randomUUID()),
                Inntektsmelding.Type.UtenArbeidsforhold(UUID.randomUUID()),
                Inntektsmelding.Type.Behandlingsdager(UUID.randomUUID()),
            ) {
                it.avsenderSystem shouldBe AvsenderSystem.nav
            }

            test("${Inntektsmelding.Type.ForespurtEkstern::class.simpleName} har egendefinert avsendersystem") {
                val eksterntAvsenderSystem =
                    AvsenderSystem(
                        orgnr = Orgnr.genererGyldig(),
                        navn = "Avbalanserte Arves Avsendersystem",
                        versjon = "AAA",
                    )

                val forespurtEkstern =
                    Inntektsmelding.Type.ForespurtEkstern(
                        id = UUID.randomUUID(),
                        _avsenderSystem = eksterntAvsenderSystem,
                    )

                forespurtEkstern.avsenderSystem shouldBe eksterntAvsenderSystem
            }
        }
    })
