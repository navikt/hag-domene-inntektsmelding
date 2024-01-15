package no.nav.helsearbeidsgiver.domene.inntektsmelding

import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.BegrunnelseIngenEllerRedusertUtbetalingKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Bonus
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Feilregistrert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferie
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.InntektEndringAarsak
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Naturalytelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NyStilling
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NyStillingsprosent
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Nyansatt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Permisjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Permittering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.RefusjonEndring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Sykefravaer
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Tariffendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.VarigLonnsendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Avsender
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.BegrunnelseRedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NaturalytelseKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykmeldt
import java.time.LocalDate
import java.util.UUID

object Utils {

    fun convertToV1(inntektsmelding: no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding {
        return Inntektsmelding(
            UUID.randomUUID(),
            Sykmeldt(inntektsmelding.identitetsnummer, inntektsmelding.fulltNavn),
            Avsender(
                inntektsmelding.orgnrUnderenhet,
                inntektsmelding.virksomhetNavn,
                "",
                inntektsmelding.innsenderNavn ?: "",
                inntektsmelding.telefonnummer ?: "",
            ),
            inntektsmelding.fraværsperioder,
            Arbeidsgiverperiode(
                inntektsmelding.arbeidsgiverperioder,
                inntektsmelding.egenmeldingsperioder,
                convertReduksjon(inntektsmelding),
            ),
            convertInntekt(inntektsmelding),
            convertRefusjon(inntektsmelding.refusjon),
            convertAarsakInnsending(inntektsmelding.årsakInnsending),
            inntektsmelding.tidspunkt,
        )
    }

    private fun convertAarsakInnsending(årsakInnsending: AarsakInnsending): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending {
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending.valueOf(årsakInnsending.value)
    }

    private fun convertRefusjon(refusjon: Refusjon): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon? {
        if (refusjon == null) {
            return null
        }
        if (refusjon.refusjonPrMnd == null) {
            // vurder å logge feil eller kast exception: skal ikke skje, men deprecated nullable-kode åpner for ugyldige data.
            return null
        }
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon(refusjon.refusjonPrMnd, convertRefusjonEndringer(refusjon.refusjonEndringer), refusjon.refusjonOpphører)
    }

    private fun convertRefusjonEndringer(refusjonEndringer: List<RefusjonEndring>?): List<no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring> {
        if (refusjonEndringer != null) {
            return refusjonEndringer.map { ref -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring(ref.beløp ?: 0.0, ref.dato ?: LocalDate.EPOCH) }.toList()
        }
        return emptyList()
    }

    fun convertInntekt(im: no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt? {
        if (im.inntekt == null) {
            throw IllegalArgumentException("Inntekt er null")
        }
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt(im.inntekt.beregnetInntekt, LocalDate.EPOCH, convertNaturalYtelser(im.naturalytelser), convertEndringAarsak(im.inntekt.endringÅrsak))
    }

    fun convertEndringAarsak(endringÅrsak: InntektEndringAarsak?): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.InntektEndringAarsak? {
        if (endringÅrsak == null) {
            return null
        }
        return when (endringÅrsak) {
            is Bonus -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus
            is Feilregistrert -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert
            is Ferie -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie(perioder = endringÅrsak.liste)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferietrekk -> Ferietrekk
            is Nyansatt -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Nyansatt
            is NyStilling -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStilling(gjelderFra = endringÅrsak.gjelderFra)
            is NyStillingsprosent -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStillingsprosent(gjelderFra = endringÅrsak.gjelderFra)
            is Permisjon -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permisjon(perioder = endringÅrsak.liste)
            is Permittering -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permittering(perioder = endringÅrsak.liste)
            is Sykefravaer -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykefravaer(perioder = endringÅrsak.liste)
            is Tariffendring -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring(gjelderFra = endringÅrsak.gjelderFra, bleKjent = endringÅrsak.bleKjent)
            is VarigLonnsendring -> no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring(gjelderFra = endringÅrsak.gjelderFra)
        }
    }

    fun convertNaturalYtelser(naturalytelser: List<Naturalytelse>?): List<no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse> {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> convertNaturalYtelse(ytelse) }.toList()
        }
        return emptyList()
    }

    private fun convertNaturalYtelse(ytelse: Naturalytelse): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse {
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse(
            NaturalytelseKode.valueOf(ytelse.naturalytelse.name),
            ytelse.beløp,
            ytelse.dato,
        )
    }

    fun convertReduksjon(im: no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding): RedusertLoennIAgp? {
        if (im.fullLønnIArbeidsgiverPerioden == null || im.fullLønnIArbeidsgiverPerioden.utbetalt == null || im.fullLønnIArbeidsgiverPerioden.begrunnelse == null) {
            return null
        }
        return RedusertLoennIAgp(im.fullLønnIArbeidsgiverPerioden.utbetalt, convertBegrunnelse(im.fullLønnIArbeidsgiverPerioden.begrunnelse))
    }

    fun convertBegrunnelse(begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode): BegrunnelseRedusertLoennIAgp {
        return BegrunnelseRedusertLoennIAgp.valueOf(begrunnelse.name)
    }
}
