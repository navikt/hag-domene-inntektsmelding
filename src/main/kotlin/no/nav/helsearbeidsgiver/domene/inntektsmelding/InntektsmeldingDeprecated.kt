@file:UseSerializers(LocalDateSerializer::class, OffsetDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.json.serializer.OffsetDateTimeSerializer
import java.time.LocalDate
import java.time.OffsetDateTime

@Deprecated("Bruk 'Inntektsmelding' istedenfor.")
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class InntektsmeldingDeprecated(
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
    val inntekt: InntektDeprecated? = null,
    val fullLønnIArbeidsgiverPerioden: FullLoennIArbeidsgiverPerioden? = null,
    val refusjon: RefusjonDeprecated,
    val naturalytelser: List<NaturalytelseDeprecated>? = null,
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
)
