package no.nav.helsearbeidsgiver.domene.inntektsmelding

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class TyppeTilAarsakKtTest : FunSpec({
    context("sjekk at Inntekt objekt blir serialisert og deserialisert riktig") {
        test("Json string med typpe verdi blir deserialisert til Inntekt objekt") {
            Json.decodeFromString(Inntekt.serializer(), Mock.typpeJson) shouldBe Mock.gyldigInntekt
        }
        test("Json string med aarsak verdi blir deserialisert til Inntekt objekt") {
            Json.decodeFromString(Inntekt.serializer(), Mock.aarsakJson) shouldBe Mock.gyldigInntekt
        }
        test("Inntekt blir serialisert til Json string med aarsak verdi") {
            Json.encodeToString(Inntekt.serializer(), Mock.gyldigInntekt) shouldBe Mock.aarsakJson
        }
    }
})

object Mock {
    val typpeJson = """{"bekreftet":true,"beregnetInntekt":1000.0,"endringÅrsak":{"typpe":"Nyansatt"},"manueltKorrigert":false}"""
    val aarsakJson = """{"bekreftet":true,"beregnetInntekt":1000.0,"endringÅrsak":{"aarsak":"Nyansatt"},"manueltKorrigert":false}"""
    val gyldigInntekt = Inntekt(
        bekreftet = true,
        beregnetInntekt = 1000.0,
        endringÅrsak = Nyansatt,
        manueltKorrigert = false,
    )
}
