package no.nav.helsearbeidsgiver.domene.inntektsmelding

import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.BegrunnelseIngenEllerRedusertUtbetalingKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Bonus
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Feilregistrert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferie
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.FullLoennIArbeidsgiverPerioden
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NaturalytelseKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykmeldt
import java.time.LocalDate
import java.util.UUID

object Utils {

    fun convertToV1(inntektsmelding: no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding): no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding {
        // TODO (inntektsmelding.behandlingsdager != null) -- må legge inn støtte for dette i nytt format
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

    fun convertToV0(im: Inntektsmelding): no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding {
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding(
            orgnrUnderenhet = im.avsender.orgnr,
            identitetsnummer = im.sykmeldt.fnr,
            fulltNavn = im.sykmeldt.navn,
            virksomhetNavn = im.avsender.orgNavn,
            behandlingsdager = emptyList(), // TODO: Brukes ikke, V1 har ikke implementert behandlingsdager
            egenmeldingsperioder = im.agp?.egenmeldinger ?: emptyList(),
            fraværsperioder = im.sykmeldingsperioder,
            arbeidsgiverperioder = im.agp?.perioder ?: emptyList(),
            beregnetInntekt = im.inntekt?.beloep ?: 0.0,
            inntektsdato = im.inntekt?.inntektsdato,
            inntekt = convertInntektToV0(im.inntekt),
            fullLønnIArbeidsgiverPerioden = convertReduksjonToV0(im.agp?.redusertLoennIAgp),
            refusjon = convertRefusjonToV0(im.refusjon),
            naturalytelser = convertNaturalYtelserToV0(im.inntekt?.naturalytelser),
            tidspunkt = im.mottatt,
            årsakInnsending = convertAarsakInnsendingToV0(im.aarsakInnsending),
            innsenderNavn = im.avsender.navn,
            telefonnummer = im.avsender.tlf,
            forespurtData = null, // Har ikke i V1....
        )
    }

    private fun convertAarsakInnsendingToV0(aarsakInnsending: no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending): AarsakInnsending {
        return AarsakInnsending.valueOf(aarsakInnsending.name.uppercase())
    }

    private fun convertNaturalYtelserToV0(naturalytelser: List<no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse>?): List<Naturalytelse>? {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> convertNaturalYtelseV0(ytelse) }.toList()
        }
        return emptyList()
    }

    private fun convertNaturalYtelseV0(ytelse: no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse): Naturalytelse {
        return Naturalytelse(
            naturalytelse = no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NaturalytelseKode.valueOf(ytelse.naturalytelse.name),
            dato = ytelse.sluttdato,
            beløp = ytelse.verdiBeloep,
        )
    }

    fun convertRefusjonToV0(refusjon: no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon?): Refusjon {
        if (refusjon == null) {
            return Refusjon(true, null, null, null)
        }
        return Refusjon(false, refusjon.beloepPerMaaned, refusjon.sluttdato, convertRefusjonEndringerToV0(refusjon.endringer))
    }

    private fun convertRefusjonEndringerToV0(endringer: List<no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring>): List<RefusjonEndring>? {
        return endringer.map { v1 ->
            RefusjonEndring(v1.beloep, v1.startdato)
        }.toList()
    }

    private fun convertReduksjonToV0(redusertLoennIAgp: RedusertLoennIAgp?): FullLoennIArbeidsgiverPerioden? {
        if (redusertLoennIAgp == null) {
            return null
        }
        return FullLoennIArbeidsgiverPerioden(false, convertReduksjonBegrunnelseToV0(redusertLoennIAgp.begrunnelse), redusertLoennIAgp.beloep)
    }

    private fun convertReduksjonBegrunnelseToV0(begrunnelse: BegrunnelseRedusertLoennIAgp): BegrunnelseIngenEllerRedusertUtbetalingKode? {
        return BegrunnelseIngenEllerRedusertUtbetalingKode.valueOf(begrunnelse.name)
    }

    fun convertInntektToV0(inntekt: Inntekt?): no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntekt? {
        if (inntekt == null) {
            return null
        }
        val korrigert = inntekt.endringAarsak != null
        return no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntekt(
            true,
            inntekt.beloep,
            convertInntektEndringAarsakToV0(inntekt.endringAarsak),
            korrigert,
        )
    }

    private fun convertInntektEndringAarsakToV0(endringAarsak: no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.InntektEndringAarsak?): InntektEndringAarsak? {
        if (endringAarsak == null) {
            return null
        }
        var inntektEndringAarsak: InntektEndringAarsak? = when (endringAarsak) {
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus -> Bonus()
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert -> Feilregistrert
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie -> Ferie(liste = endringAarsak.perioder)
            is Ferietrekk -> no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferietrekk
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Nyansatt -> Nyansatt
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStilling -> NyStilling(gjelderFra = endringAarsak.gjelderFra)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStillingsprosent -> NyStillingsprosent(gjelderFra = endringAarsak.gjelderFra)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permisjon -> Permisjon(liste = endringAarsak.perioder)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permittering -> Permittering(liste = endringAarsak.perioder)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykefravaer -> Sykefravaer(liste = endringAarsak.perioder)
            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring -> Tariffendring(
                gjelderFra = endringAarsak.gjelderFra,
                bleKjent = endringAarsak.bleKjent,
            )

            is no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring -> VarigLonnsendring(gjelderFra = endringAarsak.gjelderFra)
        }
        return inntektEndringAarsak
    }
}
