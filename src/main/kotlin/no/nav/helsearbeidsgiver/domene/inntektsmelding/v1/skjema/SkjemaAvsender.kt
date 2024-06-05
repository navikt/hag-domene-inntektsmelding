package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.FeiletValidering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.Feilmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.erGyldigTlf
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.utils.valider
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

@Serializable
data class SkjemaAvsender(
    val orgnr: Orgnr,
    val tlf: String,
) {
    internal fun valider(): List<FeiletValidering> =
        listOfNotNull(
            valider(
                vilkaar = tlf.erGyldigTlf(),
                feilmelding = Feilmelding.TLF,
            ),
        )
}
