package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class ValidateUtilsKtTest :
    FunSpec({

        context(String::erGyldigTlf.name) {
            context("gyldig") {
                withData(
                    "46865513",
                    "51231684",
                    "89796231",
                    "007621844098",
                    "000040002021",
                    "001921504679",
                    "+8909406406",
                    "+0284098400",
                    "+0132005401",
                ) {
                    it.erGyldigTlf().shouldBeTrue()
                }
            }

            context("ugyldig") {
                withData(
                    "468p2131", // med bokstav
                    "7040606", // for kort
                    "4087909846846803", // for lang
                    "00x456531369", // med bokstav
                    "0014650", // for kort
                    "00789864615619840282", // for lang
                    "+66y87963342", // med bokstav
                    "++47458699120", // med dobbel pluss
                    "+446543189", // for kort
                    "+876554321224489913", // for lang
                ) {
                    it.erGyldigTlf().shouldBeFalse()
                }
            }
        }
    })
