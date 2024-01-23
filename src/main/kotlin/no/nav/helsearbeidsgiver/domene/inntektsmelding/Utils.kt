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
                inntektsmelding.convertReduksjon(),
            ),
            convertInntekt(inntektsmelding),
            convertRefusjon(inntektsmelding.refusjon),
            convertAarsakInnsending(inntektsmelding.årsakInnsending),
            inntektsmelding.tidspunkt,
        )
    }

    private fun convertAarsakInnsending(aarsakInnsending: AarsakInnsending): AarsakInnsendingV1 {
        return AarsakInnsendingV1.valueOf(aarsakInnsending.value)
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
        return InntektV1(im.inntekt.beregnetInntekt, LocalDate.EPOCH, convertNaturalYtelser(im.naturalytelser), im.inntekt.endringÅrsak?.convert())
    }

    fun InntektEndringAarsak.convert(): InntektEndringAarsakV1 {
        return when (this) {
            is Bonus -> BonusV1
            is Feilregistrert -> FeilregistrertV1
            is Ferie -> FerieV1(perioder = this.liste)
            is Ferietrekk -> FerietrekkV1
            is Nyansatt -> NyansattV1
            is NyStilling -> NyStillingV1(gjelderFra = this.gjelderFra)
            is NyStillingsprosent -> NyStillingsprosentV1(gjelderFra = this.gjelderFra)
            is Permisjon -> PermisjonV1(perioder = this.liste)
            is Permittering -> PermitteringV1(perioder = this.liste)
            is Sykefravaer -> SykefravaerV1(perioder = this.liste)
            is Tariffendring -> TariffendringV1(gjelderFra = this.gjelderFra, bleKjent = this.bleKjent)
            is VarigLonnsendring -> VarigLoennsendringV1(gjelderFra = this.gjelderFra)
        }
    }
    fun convertNaturalYtelser(naturalytelser: List<Naturalytelse>?): List<NaturalytelseV1> {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> ytelse.convert() }.toList()
        }
        return emptyList()
    }

    fun Naturalytelse.convert(): NaturalytelseV1 {
        return NaturalytelseV1(
            NaturalytelseKodeV1.valueOf(this.naturalytelse.name),
            this.beløp,
            this.dato,
        )
    }

    fun Inntektsmelding.convertReduksjon(): RedusertLoennIAgpV1? {
        if (this.fullLønnIArbeidsgiverPerioden == null || this.fullLønnIArbeidsgiverPerioden.utbetalt == null || this.fullLønnIArbeidsgiverPerioden.begrunnelse == null) {
            return null
        }
        return RedusertLoennIAgpV1(this.fullLønnIArbeidsgiverPerioden.utbetalt, this.fullLønnIArbeidsgiverPerioden.begrunnelse.convert())
    }

    fun BegrunnelseIngenEllerRedusertUtbetalingKode.convert(): BegrunnelseRedusertLoennIAgpV1 {
        return BegrunnelseRedusertLoennIAgpV1.valueOf(this.name)
    }

    fun InntektsmeldingV1.convert(): Inntektsmelding {
        return Inntektsmelding(
            orgnrUnderenhet = this.avsender.orgnr,
            identitetsnummer = this.sykmeldt.fnr,
            fulltNavn = this.sykmeldt.navn,
            virksomhetNavn = this.avsender.orgNavn,
            behandlingsdager = emptyList(), // TODO: Brukes ikke, V1 har ikke thisplementert behandlingsdager
            egenmeldingsperioder = this.agp?.egenmeldinger ?: emptyList(),
            fraværsperioder = this.sykmeldingsperioder,
            arbeidsgiverperioder = this.agp?.perioder ?: emptyList(),
            beregnetInntekt = this.inntekt?.beloep ?: 0.0,
            inntektsdato = this.inntekt?.inntektsdato,
            inntekt = if (this.inntekt == null) { null } else { this.inntekt.convert() },
            fullLønnIArbeidsgiverPerioden = if (this.agp?.redusertLoennIAgp == null) { null } else { this.agp.redusertLoennIAgp.convert() },
            refusjon = if (this.refusjon == null) { Refusjon(true, null, null, null) } else { this.refusjon.convert() },
            naturalytelser = convertNaturalYtelserToV0(this.inntekt?.naturalytelser),
            tidspunkt = this.mottatt,
            årsakInnsending = this.aarsakInnsending.convert(),
            innsenderNavn = this.avsender.navn,
            telefonnummer = this.avsender.tlf,
            forespurtData = null, // Har ikke i V1....
        )
    }

    fun AarsakInnsendingV1.convert(): AarsakInnsending {
        return AarsakInnsending.valueOf(this.name.uppercase())
    }

    private fun convertNaturalYtelserToV0(naturalytelser: List<NaturalytelseV1>?): List<Naturalytelse>? {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> ytelse.convert() }.toList()
        }
        return emptyList()
    }

    fun NaturalytelseV1.convert(): Naturalytelse {
        return Naturalytelse(
            naturalytelse = NaturalytelseKode.valueOf(this.naturalytelse.name),
            dato = this.sluttdato,
            beløp = this.verdiBeloep,
        )
    }

    fun RefusjonV1.convert(): Refusjon {
        return Refusjon(false, this.beloepPerMaaned, this.sluttdato, this.endringer.convert())
    }

    fun List<RefusjonEndringV1>.convert(): List<RefusjonEndring> {
        return this.map { v1 ->
            RefusjonEndring(v1.beloep, v1.startdato)
        }.toList()
    }

    fun RedusertLoennIAgpV1.convert(): FullLoennIArbeidsgiverPerioden {
        return FullLoennIArbeidsgiverPerioden(false, this.begrunnelse.convert(), this.beloep)
    }

    fun BegrunnelseRedusertLoennIAgpV1.convert(): BegrunnelseIngenEllerRedusertUtbetalingKode {
        return BegrunnelseIngenEllerRedusertUtbetalingKode.valueOf(this.name)
    }

    fun InntektV1.convert(): Inntekt {
        val korrigert = this.endringAarsak != null
        return Inntekt(
            true,
            this.beloep,
            this.endringAarsak?.convert(),
            korrigert,
        )
    }

    fun InntektEndringAarsakV1.convert(): InntektEndringAarsak {
        var inntektEndringAarsak = when (this) {
            is BonusV1 -> Bonus()
            is FeilregistrertV1 -> Feilregistrert
            is FerieV1 -> Ferie(liste = this.perioder)
            is FerietrekkV1 -> Ferietrekk
            is NyansattV1 -> Nyansatt
            is NyStillingV1 -> NyStilling(gjelderFra = this.gjelderFra)
            is NyStillingsprosentV1 -> NyStillingsprosent(gjelderFra = this.gjelderFra)
            is PermisjonV1 -> Permisjon(liste = this.perioder)
            is PermitteringV1 -> Permittering(liste = this.perioder)
            is SykefravaerV1 -> Sykefravaer(liste = this.perioder)
            is TariffendringV1 -> Tariffendring(
                gjelderFra = this.gjelderFra,
                bleKjent = this.bleKjent,
            )

            is VarigLoennsendringV1 -> VarigLonnsendring(gjelderFra = this.gjelderFra)
        }
        return inntektEndringAarsak
    }
}
