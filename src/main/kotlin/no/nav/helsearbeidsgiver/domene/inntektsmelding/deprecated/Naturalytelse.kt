@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Deprecated("Bruk 'v1.Naturalytelse' istedenfor.")
@Serializable
data class Naturalytelse(
    val naturalytelse: NaturalytelseKode,
    val dato: LocalDate,
    val beløp: Double,
)

@Deprecated("Bruk 'v1.NaturalytelseKode' istedenfor.")
@Serializable
enum class NaturalytelseKode {
    AKSJERGRUNNFONDSBEVISTILUNDERKURS,
    ANNET,
    BEDRIFTSBARNEHAGEPLASS,
    BESOEKSREISERHJEMMETANNET,
    BIL,
    BOLIG,
    ELEKTRONISKKOMMUNIKASJON,
    FRITRANSPORT,
    INNBETALINGTILUTENLANDSKPENSJONSORDNING,
    KOSTBESPARELSEIHJEMMET,
    KOSTDAGER,
    KOSTDOEGN,
    LOSJI,
    OPSJONER,
    RENTEFORDELLAAN,
    SKATTEPLIKTIGDELFORSIKRINGER,
    TILSKUDDBARNEHAGEPLASS,
    YRKEBILTJENESTLIGBEHOVKILOMETER,
    YRKEBILTJENESTLIGBEHOVLISTEPRIS,
}
