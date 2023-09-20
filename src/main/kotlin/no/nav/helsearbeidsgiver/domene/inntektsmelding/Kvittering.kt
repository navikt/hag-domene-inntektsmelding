@file:UseSerializers(LocalDateSerializer::class, OffsetDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import java.time.LocalDate
import java.time.OffsetDateTime

@Serializable
data class Kvittering(
    val kvitteringDokument: KvitteringSimba? = null,
    val kvitteringEkstern: KvitteringEkstern? = null,
)

@Serializable
data class KvitteringSimba(
    val orgnrUnderenhet: String,
    val identitetsnummer: String,
    val fulltNavn: String,
    val telefonnummer: String? = null,
    val innsenderNavn: String? = null,
    val virksomhetNavn: String,
    val behandlingsdager: List<LocalDate>,
    val egenmeldingsperioder: List<Periode>,
    val arbeidsgiverperioder: List<Periode>,
    val bestemmendeFraværsdag: LocalDate,
    val fraværsperioder: List<Periode>,
    val inntekt: Inntekt,
    val fullLønnIArbeidsgiverPerioden: FullLoennIArbeidsgiverPerioden? = null,
    val refusjon: Refusjon,
    val naturalytelser: List<Naturalytelse>? = null,
    val årsakInnsending: AarsakInnsending,
    val bekreftOpplysninger: Boolean,
    val tidspunkt: OffsetDateTime,
    val forespurtData: List<String>? = null,
)

@Serializable
data class KvitteringEkstern(
    val avsenderSystem: String,
    val referanse: String,
    val tidspunkt: OffsetDateTime,
)
