@file:UseSerializers(LocalDateSerializer::class, OffsetDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import java.time.LocalDate
import java.time.OffsetDateTime

@Serializable
data class Inntektsmelding(
    val orgnrUnderenhet: String,
    val identitetsnummer: String,
    val fulltNavn: String,
    val virksomhetNavn: String,
    val behandlingsdager: List<LocalDate>,
    val egenmeldingsperioder: List<Periode>,
    val bestemmendeFraværsdag: LocalDate,
    val fraværsperioder: List<Periode>,
    val arbeidsgiverperioder: List<Periode>,
    val beregnetInntekt: Double,
    val inntekt: Inntekt? = null,
    val fullLønnIArbeidsgiverPerioden: FullLoennIArbeidsgiverPerioden? = null,
    val refusjon: Refusjon,
    val naturalytelser: List<Naturalytelse>? = null,
    val tidspunkt: OffsetDateTime,
    val årsakInnsending: AarsakInnsending,
    val innsenderNavn: String? = null,
    val telefonnummer: String? = null,
    val forespurtData: List<String>? = null,
)
