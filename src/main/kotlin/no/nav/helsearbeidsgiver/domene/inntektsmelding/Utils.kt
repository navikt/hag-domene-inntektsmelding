package no.nav.helsearbeidsgiver.domene.inntektsmelding

import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.BegrunnelseIngenEllerRedusertUtbetalingKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Bonus
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Feilregistrert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferie
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferietrekk
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.FullLoennIArbeidsgiverPerioden
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.InntektEndringAarsak
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Naturalytelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NaturalytelseKode
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
import java.time.LocalDate
import java.util.UUID
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending as AarsakInnsendingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode as ArbeidsgiverperiodeV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Avsender as AvsenderV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.BegrunnelseRedusertLoennIAgp as BegrunnelseRedusertLoennIAgpV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus as BonusV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert as FeilregistrertV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie as FerieV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk as FerietrekkV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt as InntektV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.InntektEndringAarsak as InntektEndringAarsakV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding as InntektsmeldingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse as NaturalytelseV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NaturalytelseKode as NaturalytelseKodeV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStilling as NyStillingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStillingsprosent as NyStillingsprosentV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Nyansatt as NyansattV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permisjon as PermisjonV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permittering as PermitteringV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp as RedusertLoennIAgpV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon as RefusjonV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring as RefusjonEndringV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykefravaer as SykefravaerV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykmeldt as SykmeldtV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring as TariffendringV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring as VarigLoennsendringV1

object Utils {

    fun convertToV1(inntektsmelding: Inntektsmelding): InntektsmeldingV1 {
        // TODO (inntektsmelding.behandlingsdager != null) -- må legge inn støtte for dette i nytt format
        return InntektsmeldingV1(
            UUID.randomUUID(),
            SykmeldtV1(inntektsmelding.identitetsnummer, inntektsmelding.fulltNavn),
            AvsenderV1(
                inntektsmelding.orgnrUnderenhet,
                inntektsmelding.virksomhetNavn,
                "",
                inntektsmelding.innsenderNavn ?: "",
                inntektsmelding.telefonnummer ?: "",
            ),
            inntektsmelding.fraværsperioder,
            ArbeidsgiverperiodeV1(
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

    private fun convertAarsakInnsending(årsakInnsending: AarsakInnsending): AarsakInnsendingV1 {
        return AarsakInnsendingV1.valueOf(årsakInnsending.value)
    }

    private fun convertRefusjon(refusjon: Refusjon): RefusjonV1? {
        if (refusjon.refusjonPrMnd == null) {
            // vurder å logge feil eller kast exception: skal ikke skje, men deprecated nullable-kode åpner for ugyldige data.
            return null
        }
        return RefusjonV1(refusjon.refusjonPrMnd, convertRefusjonEndringer(refusjon.refusjonEndringer), refusjon.refusjonOpphører)
    }

    private fun convertRefusjonEndringer(refusjonEndringer: List<RefusjonEndring>?): List<RefusjonEndringV1> {
        if (refusjonEndringer != null) {
            return refusjonEndringer.map { ref -> RefusjonEndringV1(ref.beløp ?: 0.0, ref.dato ?: LocalDate.EPOCH) }.toList()
        }
        return emptyList()
    }

    fun convertInntekt(im: Inntektsmelding): InntektV1? {
        if (im.inntekt == null) {
            throw IllegalArgumentException("Inntekt er null")
        }
        return InntektV1(im.inntekt.beregnetInntekt, LocalDate.EPOCH, convertNaturalYtelser(im.naturalytelser), convertEndringAarsak(im.inntekt.endringÅrsak))
    }

    fun convertEndringAarsak(endringÅrsak: InntektEndringAarsak?): InntektEndringAarsakV1? {
        if (endringÅrsak == null) {
            return null
        }
        return when (endringÅrsak) {
            is Bonus -> BonusV1
            is Feilregistrert -> FeilregistrertV1
            is Ferie -> FerieV1(perioder = endringÅrsak.liste)
            is Ferietrekk -> FerietrekkV1
            is Nyansatt -> NyansattV1
            is NyStilling -> NyStillingV1(gjelderFra = endringÅrsak.gjelderFra)
            is NyStillingsprosent -> NyStillingsprosentV1(gjelderFra = endringÅrsak.gjelderFra)
            is Permisjon -> PermisjonV1(perioder = endringÅrsak.liste)
            is Permittering -> PermitteringV1(perioder = endringÅrsak.liste)
            is Sykefravaer -> SykefravaerV1(perioder = endringÅrsak.liste)
            is Tariffendring -> TariffendringV1(gjelderFra = endringÅrsak.gjelderFra, bleKjent = endringÅrsak.bleKjent)
            is VarigLonnsendring -> VarigLoennsendringV1(gjelderFra = endringÅrsak.gjelderFra)
        }
    }

    fun convertNaturalYtelser(naturalytelser: List<Naturalytelse>?): List<NaturalytelseV1> {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> convertNaturalYtelse(ytelse) }.toList()
        }
        return emptyList()
    }

    private fun convertNaturalYtelse(ytelse: Naturalytelse): NaturalytelseV1 {
        return NaturalytelseV1(
            NaturalytelseKodeV1.valueOf(ytelse.naturalytelse.name),
            ytelse.beløp,
            ytelse.dato,
        )
    }

    fun convertReduksjon(im: Inntektsmelding): RedusertLoennIAgpV1? {
        if (im.fullLønnIArbeidsgiverPerioden == null || im.fullLønnIArbeidsgiverPerioden.utbetalt == null || im.fullLønnIArbeidsgiverPerioden.begrunnelse == null) {
            return null
        }
        return RedusertLoennIAgpV1(im.fullLønnIArbeidsgiverPerioden.utbetalt, convertBegrunnelse(im.fullLønnIArbeidsgiverPerioden.begrunnelse))
    }

    fun convertBegrunnelse(begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode): BegrunnelseRedusertLoennIAgpV1 {
        return BegrunnelseRedusertLoennIAgpV1.valueOf(begrunnelse.name)
    }

    fun convertToV0(im: InntektsmeldingV1): Inntektsmelding {
        return Inntektsmelding(
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

    private fun convertAarsakInnsendingToV0(aarsakInnsending: AarsakInnsendingV1): AarsakInnsending {
        return AarsakInnsending.valueOf(aarsakInnsending.name.uppercase())
    }

    private fun convertNaturalYtelserToV0(naturalytelser: List<NaturalytelseV1>?): List<Naturalytelse>? {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> convertNaturalYtelseV0(ytelse) }.toList()
        }
        return emptyList()
    }

    private fun convertNaturalYtelseV0(ytelse: NaturalytelseV1): Naturalytelse {
        return Naturalytelse(
            naturalytelse = NaturalytelseKode.valueOf(ytelse.naturalytelse.name),
            dato = ytelse.sluttdato,
            beløp = ytelse.verdiBeloep,
        )
    }

    fun convertRefusjonToV0(refusjon: RefusjonV1?): Refusjon {
        if (refusjon == null) {
            return Refusjon(true, null, null, null)
        }
        return Refusjon(false, refusjon.beloepPerMaaned, refusjon.sluttdato, convertRefusjonEndringerToV0(refusjon.endringer))
    }

    private fun convertRefusjonEndringerToV0(endringer: List<RefusjonEndringV1>): List<RefusjonEndring>? {
        return endringer.map { v1 ->
            RefusjonEndring(v1.beloep, v1.startdato)
        }.toList()
    }

    private fun convertReduksjonToV0(redusertLoennIAgp: RedusertLoennIAgpV1?): FullLoennIArbeidsgiverPerioden? {
        if (redusertLoennIAgp == null) {
            return null
        }
        return FullLoennIArbeidsgiverPerioden(false, convertReduksjonBegrunnelseToV0(redusertLoennIAgp.begrunnelse), redusertLoennIAgp.beloep)
    }

    private fun convertReduksjonBegrunnelseToV0(begrunnelse: BegrunnelseRedusertLoennIAgpV1): BegrunnelseIngenEllerRedusertUtbetalingKode? {
        return BegrunnelseIngenEllerRedusertUtbetalingKode.valueOf(begrunnelse.name)
    }

    fun convertInntektToV0(inntekt: InntektV1?): Inntekt? {
        if (inntekt == null) {
            return null
        }
        val korrigert = inntekt.endringAarsak != null
        return Inntekt(
            true,
            inntekt.beloep,
            convertInntektEndringAarsakToV0(inntekt.endringAarsak),
            korrigert,
        )
    }

    private fun convertInntektEndringAarsakToV0(endringAarsak: InntektEndringAarsakV1?): InntektEndringAarsak? {
        if (endringAarsak == null) {
            return null
        }
        var inntektEndringAarsak: InntektEndringAarsak? = when (endringAarsak) {
            is BonusV1 -> Bonus()
            is FeilregistrertV1 -> Feilregistrert
            is FerieV1 -> Ferie(liste = endringAarsak.perioder)
            is FerietrekkV1 -> Ferietrekk
            is NyansattV1 -> Nyansatt
            is NyStillingV1 -> NyStilling(gjelderFra = endringAarsak.gjelderFra)
            is NyStillingsprosentV1 -> NyStillingsprosent(gjelderFra = endringAarsak.gjelderFra)
            is PermisjonV1 -> Permisjon(liste = endringAarsak.perioder)
            is PermitteringV1 -> Permittering(liste = endringAarsak.perioder)
            is SykefravaerV1 -> Sykefravaer(liste = endringAarsak.perioder)
            is TariffendringV1 -> Tariffendring(
                gjelderFra = endringAarsak.gjelderFra,
                bleKjent = endringAarsak.bleKjent,
            )

            is VarigLoennsendringV1 -> VarigLonnsendring(gjelderFra = endringAarsak.gjelderFra)
        }
        return inntektEndringAarsak
    }
}
