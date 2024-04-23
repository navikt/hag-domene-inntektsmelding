package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erGyldigTlf
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

@Serializable
data class SkjemaAvsender(
    val orgnr: String,
    val tlf: String,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            valider(
                vilkaar = Orgnr.erGyldig(orgnr),
                feilmelding = Feilmelding.ORGNR,
            ),

            valider(
                vilkaar = tlf.erGyldigTlf(),
                feilmelding = Feilmelding.TLF,
            ),
        )
}
