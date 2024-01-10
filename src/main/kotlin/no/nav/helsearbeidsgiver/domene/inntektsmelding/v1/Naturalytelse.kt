@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class Naturalytelse(
    val naturalytelse: NaturalytelseKode,
    val verdiBeloep: Double,
    val sluttdato: LocalDate,
)

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
