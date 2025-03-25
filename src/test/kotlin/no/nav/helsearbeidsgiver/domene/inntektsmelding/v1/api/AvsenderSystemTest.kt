package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.utils.json.toJson
import java.util.UUID

class AvsenderSystemTest : FunSpec({

    test("serialiser custom Avsender") {
        val customAvsenderSystem = AvsenderSystem(avsenderSystemNavn = "Kontor")
        val customSerial = customAvsenderSystem.toJson(AvsenderSystem.serializer())
        customSerial.toString() shouldContain (Regex(customAvsenderSystem.avsenderSystemNavn))
    }

    test("serialiser avsender defaultverdi ") {
        val type = Inntektsmelding.Type.Forespurt(id = UUID.randomUUID())
        type.avsenderSystem shouldBe AvsenderSystem()
        val streng = type.toJson(Inntektsmelding.Type.serializer())
        streng.toString().shouldContain(Regex(NAV_ORGNR.toString()))
    }
})
