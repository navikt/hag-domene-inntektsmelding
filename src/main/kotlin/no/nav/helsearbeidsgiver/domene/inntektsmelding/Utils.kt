@file:Suppress("DEPRECATION")

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
import no.nav.helsearbeidsgiver.utils.pipe.orDefault
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.util.UUID
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending as AarsakInnsendingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode as ArbeidsgiverperiodeV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Avsender as AvsenderV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus as BonusV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert as FeilregistrertV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie as FerieV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk as FerietrekkV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt as InntektV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.InntektEndringAarsak as InntektEndringAarsakV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding as InntektsmeldingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse as NaturalytelseV1
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
    fun Inntektsmelding.convertToV1(
        inntektsmeldingId: UUID,
        type: InntektsmeldingV1.Type,
    ): InntektsmeldingV1 {
        // kutt ut feltet hvis det ikke finnes i forespurt data:
        val agpV1 =
            if (forespurtData?.contains("arbeidsgiverperiode") == true) {
                convertAgp()
            } else {
                null
            }

        val inntektV1 =
            if (forespurtData?.contains("inntekt") == true) {
                convertInntekt()
            } else {
                null
            }

        val refusjonV1 =
            if (forespurtData?.contains("refusjon") == true) {
                refusjon.convert()
            } else {
                null
            }

        return InntektsmeldingV1(
            id = inntektsmeldingId,
            type = type,
            sykmeldt =
            SykmeldtV1(
                fnr = identitetsnummer.let(::Fnr),
                navn = fulltNavn,
            ),
            avsender =
            AvsenderV1(
                orgnr = orgnrUnderenhet.let(::Orgnr),
                orgNavn = virksomhetNavn,
                navn = innsenderNavn.orEmpty(),
                tlf = telefonnummer.orEmpty(),
            ),
            sykmeldingsperioder = fraværsperioder,
            agp = agpV1,
            inntekt = inntektV1,
            refusjon = refusjonV1,
            aarsakInnsending = årsakInnsending.convert(),
            mottatt = tidspunkt,
            vedtaksperiodeId = vedtaksperiodeId,
        )
    }

    fun Inntektsmelding.convertAgp(): ArbeidsgiverperiodeV1 =
        ArbeidsgiverperiodeV1(
            perioder = arbeidsgiverperioder,
            egenmeldinger = egenmeldingsperioder,
            redusertLoennIAgp = fullLønnIArbeidsgiverPerioden?.convert(),
        )

    fun FullLoennIArbeidsgiverPerioden.convert(): RedusertLoennIAgpV1? =
        if (utbetalerFullLønn || utbetalt == null || begrunnelse == null) {
            null
        } else {
            RedusertLoennIAgpV1(
                beloep = utbetalt,
                begrunnelse = begrunnelse.convert(),
            )
        }

    fun Inntektsmelding.convertInntekt(): InntektV1 =
        if (inntekt == null) {
            throw IllegalArgumentException("Inntekt er null")
        } else {
            InntektV1(
                beloep = inntekt.beregnetInntekt,
                inntektsdato = inntektsdato.orDefault(bestemmendeFraværsdag),
                naturalytelser = naturalytelser?.map { it.convert() }.orEmpty(),
                endringAarsak = inntekt.endringÅrsak?.convert(),
                endringAarsaker = listOfNotNull(inntekt.endringÅrsak?.convert()),
            )
        }

    fun InntektEndringAarsak.convert(): InntektEndringAarsakV1 =
        when (this) {
            is Bonus -> BonusV1
            is Feilregistrert -> FeilregistrertV1
            is Ferie -> FerieV1(ferier = liste)
            is Ferietrekk -> FerietrekkV1
            is Nyansatt -> NyansattV1
            is NyStilling -> NyStillingV1(gjelderFra = this.gjelderFra)
            is NyStillingsprosent -> NyStillingsprosentV1(gjelderFra = this.gjelderFra)
            is Permisjon -> PermisjonV1(permisjoner = liste)
            is Permittering -> PermitteringV1(permitteringer = liste)
            is Sykefravaer -> SykefravaerV1(sykefravaer = liste)
            is Tariffendring -> TariffendringV1(gjelderFra = this.gjelderFra, bleKjent = this.bleKjent)
            is VarigLonnsendring -> VarigLoennsendringV1(gjelderFra = this.gjelderFra)
        }

    fun Naturalytelse.convert(): NaturalytelseV1 =
        NaturalytelseV1(
            NaturalytelseV1.Kode.valueOf(this.naturalytelse.name),
            this.beløp,
            this.dato,
        )

    fun BegrunnelseIngenEllerRedusertUtbetalingKode.convert(): RedusertLoennIAgpV1.Begrunnelse =
        RedusertLoennIAgpV1.Begrunnelse.valueOf(this.name)

    fun Refusjon.convert(): RefusjonV1? =
        // (refusjonPrMnd == null) bør ikke skje, men deprecated nullable-kode åpner for ugyldige data.
        if (!utbetalerHeleEllerDeler || refusjonPrMnd == null) {
            null
        } else {
            RefusjonV1(
                beloepPerMaaned = refusjonPrMnd,
                endringer = refusjonEndringer?.mapNotNull { it.convert() }.orEmpty(),
                sluttdato = refusjonOpphører,
            )
        }

    private fun RefusjonEndring.convert(): RefusjonEndringV1? =
        if (beløp == null || dato == null) {
            null
        } else {
            RefusjonEndringV1(beløp, dato)
        }

    private fun AarsakInnsending.convert(): AarsakInnsendingV1 = AarsakInnsendingV1.valueOf(value)

    fun InntektsmeldingV1.convert(): Inntektsmelding =
        Inntektsmelding(
            orgnrUnderenhet = avsender.orgnr.verdi,
            identitetsnummer = sykmeldt.fnr.verdi,
            fulltNavn = sykmeldt.navn,
            virksomhetNavn = avsender.orgNavn,
            // Brukes ikke, V1 har ikke implementert behandlingsdager
            behandlingsdager = emptyList(),
            egenmeldingsperioder = agp?.egenmeldinger.orEmpty(),
            fraværsperioder = sykmeldingsperioder,
            arbeidsgiverperioder = agp?.perioder.orEmpty(),
            beregnetInntekt = inntekt?.beloep ?: 0.0,
            inntektsdato = inntekt?.inntektsdato,
            inntekt = inntekt?.convert(),
            fullLønnIArbeidsgiverPerioden =
            agp?.redusertLoennIAgp?.convert().orDefault(
                FullLoennIArbeidsgiverPerioden(
                    utbetalerFullLønn = true,
                    begrunnelse = null,
                    utbetalt = null,
                ),
            ),
            refusjon = refusjon?.convert() ?: Refusjon(false, null, null, null),
            naturalytelser = convertNaturalYtelserToV0(inntekt?.naturalytelser),
            tidspunkt = mottatt,
            årsakInnsending = aarsakInnsending.convert(),
            innsenderNavn = avsender.navn,
            telefonnummer = avsender.tlf,
            forespurtData = getForespurtData(),
            vedtaksperiodeId = vedtaksperiodeId,
        )

    fun InntektsmeldingV1.getForespurtData(): List<String> {
        val fullListe = mapOf("arbeidsgiverperiode" to this.agp, "inntekt" to this.inntekt, "refusjon" to this.refusjon)
        return fullListe.filterValues { it != null }.keys.toList()
    }

    fun AarsakInnsendingV1.convert(): AarsakInnsending = AarsakInnsending.valueOf(this.name.uppercase())

    private fun convertNaturalYtelserToV0(naturalytelser: List<NaturalytelseV1>?): List<Naturalytelse> {
        if (naturalytelser != null) {
            return naturalytelser.map { ytelse -> ytelse.convert() }.toList()
        }
        return emptyList()
    }

    fun NaturalytelseV1.convert(): Naturalytelse =
        Naturalytelse(
            naturalytelse = NaturalytelseKode.valueOf(this.naturalytelse.name),
            dato = this.sluttdato,
            beløp = this.verdiBeloep,
        )

    fun RefusjonV1.convert(): Refusjon = Refusjon(true, this.beloepPerMaaned, this.sluttdato, this.endringer.convert())

    fun List<RefusjonEndringV1>.convert(): List<RefusjonEndring> =
        this.map { v1 ->
            RefusjonEndring(v1.beloep, v1.startdato)
        }

    fun RedusertLoennIAgpV1.convert(): FullLoennIArbeidsgiverPerioden =
        FullLoennIArbeidsgiverPerioden(false, this.begrunnelse.convert(), this.beloep)

    fun RedusertLoennIAgpV1.Begrunnelse.convert(): BegrunnelseIngenEllerRedusertUtbetalingKode =
        BegrunnelseIngenEllerRedusertUtbetalingKode.valueOf(this.name)

    fun InntektV1.convert(): Inntekt {
        val korrigert = this.endringAarsak != null
        return Inntekt(
            true,
            this.beloep,
            this.endringAarsak?.convert(),
            korrigert,
        )
    }

    fun InntektEndringAarsakV1.convert(): InntektEndringAarsak =
        when (this) {
            is BonusV1 -> Bonus()
            is FeilregistrertV1 -> Feilregistrert
            is FerieV1 -> Ferie(liste = ferier)
            is FerietrekkV1 -> Ferietrekk
            is NyansattV1 -> Nyansatt
            is NyStillingV1 -> NyStilling(gjelderFra = this.gjelderFra)
            is NyStillingsprosentV1 -> NyStillingsprosent(gjelderFra = this.gjelderFra)
            is PermisjonV1 -> Permisjon(liste = permisjoner)
            is PermitteringV1 -> Permittering(liste = permitteringer)
            is SykefravaerV1 -> Sykefravaer(liste = sykefravaer)
            is TariffendringV1 ->
                Tariffendring(
                    gjelderFra = this.gjelderFra,
                    bleKjent = this.bleKjent,
                )

            is VarigLoennsendringV1 -> VarigLonnsendring(gjelderFra = this.gjelderFra)
        }
}
