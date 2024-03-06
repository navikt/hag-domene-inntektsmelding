@file:UseSerializers(LocalDateSerializer::class, OffsetDateTimeSerializer::class, UuidSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.bestemmendeFravaersdag
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.UuidSerializer
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Deprecated("Bruk 'v1.Inntektsmelding' istedenfor.")
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Inntektsmelding(
    val orgnrUnderenhet: String,
    val identitetsnummer: String,
    val fulltNavn: String,
    val virksomhetNavn: String,
    val behandlingsdager: List<LocalDate>,
    val egenmeldingsperioder: List<Periode>,
    val fraværsperioder: List<Periode>,
    val arbeidsgiverperioder: List<Periode>,
    val beregnetInntekt: Double,
    val inntektsdato: LocalDate? = null,
    val inntekt: Inntekt? = null,
    val fullLønnIArbeidsgiverPerioden: FullLoennIArbeidsgiverPerioden? = null,
    val refusjon: Refusjon,
    val naturalytelser: List<Naturalytelse>? = null,
    val tidspunkt: OffsetDateTime,
    val årsakInnsending: AarsakInnsending,
    val innsenderNavn: String? = null,
    val telefonnummer: String? = null,
    val forespurtData: List<String>? = null,
    @EncodeDefault
    val bestemmendeFraværsdag: LocalDate = bestemmendeFravaersdag(
        arbeidsgiverperioder = arbeidsgiverperioder,
        egenmeldingsperioder = egenmeldingsperioder,
        sykmeldingsperioder = fraværsperioder,
    ),
    val vedtaksperiodeId: UUID?,
)
