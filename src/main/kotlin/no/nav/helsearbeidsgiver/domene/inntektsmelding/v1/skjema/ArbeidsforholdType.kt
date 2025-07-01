@file:UseSerializers(UuidSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.util.UUID

@Serializable
sealed class ArbeidsforholdType {
    @Serializable
    @SerialName("MedArbeidsforhold")
    data class MedArbeidsforhold(
        val vedtaksperiodeId: UUID,
    ) : ArbeidsforholdType()

    @Serializable
    @SerialName("Fisker")
    object Fisker : ArbeidsforholdType()

    @Serializable
    @SerialName("UtenArbeidsforhold")
    object UtenArbeidsforhold : ArbeidsforholdType()
}
