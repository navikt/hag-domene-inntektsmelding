package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ErGyldigYrkesbeskrivelseTest :
    FunSpec({

        context("Yrkesbeskrivelse skal inneholde kun lovlige tegn og ha max lengde 128, eller være blank") {
            withData(
                nameFn = { (yrkesbeskrivelse, gyldig) ->
                    "$yrkesbeskrivelse er $gyldig"
                },
                "Snekker 1234" to true,
                "TESTROLLE/ANNENTESTROLLE(FIKTIVE TESTORGANISASJONER)" to true,
                "1.konsulent" to true,
                "Maler (lærling)" to true,
                "bla" to true,
                "" to true,
                " " to true,
                "Fürstekakebaker" to true,
                "Førstemaskinist på båt, liten" to true,
                "Tømmer-hogger" to true,
                "Frédsforsker" to true,
                "Frißör" to true,
                "Select * from inntektsmelding;" to false,
                "\"tøys\"" to false,
                "alert('javascript')" to false,
                "#!/bin/bash" to false,
                "}" to false,
                "{" to false,
                "+" to false,
                "<" to false,
                ">" to false,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaltfor" +
                    " altfor altfor altfor lang tekst, mye lengre enn grensen på 128 tegn" to false,
            ) { (yrkesbeskrivelse, gyldig) ->
                yrkesbeskrivelse.erGyldigYrkesbeskrivelse() shouldBe gyldig
            }
        }
    })
