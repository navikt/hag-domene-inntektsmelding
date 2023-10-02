package no.nav.helsearbeidsgiver.domene.inntektsmelding

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.utils.json.fromJson

class BegrunnelseIngenEllerRedusertUtbetalingKodeTest : FunSpec({
    context("deserialisering") {
        withData(
            "\"ArbeidOpphoert\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.ARBEID_OPPHOERT,
            "\"Beskjed_Gitt_For_Sent\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.BESKJED_GITT_FOR_SENT,
            "\"betvilerarbeidsufoerhet\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.BETVILER_ARBEIDSUFOERHET,
            "\"FERIEELLERAVSPASERING\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.FERIE_ELLER_AVSPASERING,
            "\"SaerREGLER\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.SAERREGLER,
            "\"${BegrunnelseIngenEllerRedusertUtbetalingKode.BETVILER_ARBEIDSUFOERHET.name}\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.BETVILER_ARBEIDSUFOERHET,
            "\"${BegrunnelseIngenEllerRedusertUtbetalingKode.SAERREGLER.value}\"" to BegrunnelseIngenEllerRedusertUtbetalingKode.SAERREGLER,
        ) { (json, expected) ->
            val actual = json.fromJson(BegrunnelseIngenEllerRedusertUtbetalingKode.serializer())

            actual shouldBe expected
        }
    }
})
