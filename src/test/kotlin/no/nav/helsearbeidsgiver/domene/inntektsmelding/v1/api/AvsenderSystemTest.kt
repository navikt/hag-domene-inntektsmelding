package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.decodeFromJsonElement
import no.nav.helsearbeidsgiver.utils.json.jsonConfig
import no.nav.helsearbeidsgiver.utils.json.toJson

class AvsenderSystemTest : FunSpec({

    test("serialize default verdier") {
        val defaultAvsenderSystem = AvsenderSystem()
        val avsenderSystem = jsonConfig.decodeFromJsonElement<AvsenderSystem>(defaultAvsenderSystem.toJson(AvsenderSystem.serializer()))
        avsenderSystem shouldBe AvsenderSystem()
        val customAvsenderSystem = AvsenderSystem(avsenderSystemNavn = "Kontor")
        val customSerial = jsonConfig.decodeFromJsonElement<AvsenderSystem>(customAvsenderSystem.toJson(AvsenderSystem.serializer()))
        customSerial.avsenderSystemNavn shouldBe customAvsenderSystem.avsenderSystemNavn
    }
})
