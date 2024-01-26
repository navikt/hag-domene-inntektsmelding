package no.nav.helsearbeidsgiver.domene.inntektsmelding

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertInntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertNaturalYtelser
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertReduksjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertToV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.getForespurtData
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.BegrunnelseIngenEllerRedusertUtbetalingKode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Bonus
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Feilregistrert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferie
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Ferietrekk
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.FullLoennIArbeidsgiverPerioden
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Inntekt
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending as AarsakInnsendingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode as ArbeidsgiverperiodeV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.BegrunnelseRedusertLoennIAgp as BegrunnelseRedusertLoennIAgpV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus as BonusV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert as FeilregistrertV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie as FerieV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk as FerietrekkV1
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring as TariffendringV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring as VarigLoennsendringV1

class UtilsTest : FunSpec({

    val dato = LocalDate.of(2023, 1, 1)
    val nyID = UUID.randomUUID()

    test("convertInntekt") {
        val im = lagGammelInntektsmelding()
        val nyInntekt = convertInntekt(im)
        nyInntekt shouldNotBe null
        val gammelInntekt = im.inntekt
        nyInntekt?.beloep shouldBe gammelInntekt?.beregnetInntekt
    }

    test("convertInntekt kaster exception hvis null") {
        val im = lagGammelInntektsmelding().copy(inntekt = null)
        shouldThrowExactly<IllegalArgumentException> {
            convertInntekt(im)
        }
    }

    test("convertEndringAarsak") {
        Bonus(1.0, LocalDate.EPOCH).convert() shouldBe BonusV1
        Bonus(null, null).convert() shouldBe BonusV1
        Feilregistrert.convert() shouldBe FeilregistrertV1
        Ferie(lagPeriode()).convert() shouldBe FerieV1(lagPeriode())
        Ferietrekk.convert() shouldBe FerietrekkV1
        Nyansatt.convert() shouldBe NyansattV1
        NyStilling(dato).convert() shouldBe NyStillingV1(dato)
        NyStillingsprosent(dato).convert() shouldBe NyStillingsprosentV1(dato)
        Permisjon(lagPeriode()).convert() shouldBe PermisjonV1(lagPeriode())
        Permittering(lagPeriode()).convert() shouldBe PermitteringV1(lagPeriode())
        Sykefravaer(lagPeriode()).convert() shouldBe SykefravaerV1(lagPeriode())
        Tariffendring(dato, dato).convert() shouldBe TariffendringV1(dato, dato)
        VarigLonnsendring(dato).convert() shouldBe VarigLoennsendringV1(dato)
    }

    test("convertNaturalYtelse") {
        val belop = 10.0
        val gamleYtelser = NaturalytelseKode.entries.map {
            Naturalytelse(it, dato, belop)
        }.toList()
        val nyeYtelser = NaturalytelseKodeV1.entries.map {
            NaturalytelseV1(it, belop, dato)
        }.toList()
        convertNaturalYtelser(gamleYtelser) shouldBeEqual nyeYtelser
    }

    test("convertReduksjon") {
        lagGammelInntektsmelding().convertReduksjon() shouldBe null
        val utbetalt = 10000.0
        val im_med_reduksjon = lagGammelInntektsmelding().copy(
            fullLønnIArbeidsgiverPerioden =
            FullLoennIArbeidsgiverPerioden(
                false,
                BegrunnelseIngenEllerRedusertUtbetalingKode.BetvilerArbeidsufoerhet,
                utbetalt,
            ),
        )
        val agp = convertToV1(im_med_reduksjon, nyID).agp
        agp?.redusertLoennIAgp?.beloep shouldBe utbetalt
        agp?.redusertLoennIAgp?.begrunnelse shouldBe BegrunnelseRedusertLoennIAgpV1.BetvilerArbeidsufoerhet
    }

    test("convertBegrunnelse") {
        val gamleBegrunnelser = BegrunnelseIngenEllerRedusertUtbetalingKode.entries.toList()
        val nyeBegrunnelser = BegrunnelseRedusertLoennIAgpV1.entries.toList()
        gamleBegrunnelser.forEachIndexed { index, begrunnelse -> begrunnelse.convert() shouldBe nyeBegrunnelser[index] }
    }

    test("håndterer tomme lister og null-verdier") {
        val im = convertToV1(lagGammelInntektsmeldingMedTommeOgNullVerdier(), nyID)
        im.aarsakInnsending shouldBe AarsakInnsendingV1.Endring
    }

    test("konverter im til V1") {
        val gammelIM = lagGammelInntektsmelding()
        val nyIM = convertToV1(gammelIM, nyID)
        nyIM.sykmeldt.fnr shouldBe gammelIM.identitetsnummer
        nyIM.sykmeldt.navn shouldBe gammelIM.fulltNavn

        nyIM.avsender.navn shouldBe gammelIM.innsenderNavn
        nyIM.avsender.orgNavn shouldBe gammelIM.virksomhetNavn
        nyIM.avsender.orgnr shouldBe gammelIM.orgnrUnderenhet
        nyIM.avsender.tlf shouldBe gammelIM.telefonnummer

        nyIM.aarsakInnsending shouldBe AarsakInnsendingV1.Ny
    }

    test("konverter fra nytt til gammelt IM-format") {
        val orginal = lagGammelInntektsmelding()
        val nyIM = convertToV1(orginal, nyID)
        val gammelIM = nyIM.convert()
        gammelIM.shouldBeEqualToIgnoringFields(orginal, Inntektsmelding::inntektsdato, Inntektsmelding::naturalytelser, Inntektsmelding::fullLønnIArbeidsgiverPerioden)
        // konvertering setter inntektsdato til epoch-tid og naturalytelse til tom liste, fullLønnIAgp som null-verdi i orginal blir oversatt til FullLoennIAGP(true, null, null)
        gammelIM.inntektsdato shouldBe LocalDate.EPOCH
        gammelIM.naturalytelser shouldBe emptyList()
        gammelIM.fullLønnIArbeidsgiverPerioden shouldBe FullLoennIArbeidsgiverPerioden(true, null, null)
    }

    test("konverter inntekt fra nytt til gammelt IM-format") {
        val belop = 1000.0
        val dato = LocalDate.of(2024, 1, 1)
        val nyInntekt = no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt(
            belop,
            dato,
            listOf(NaturalytelseV1(NaturalytelseKodeV1.BEDRIFTSBARNEHAGEPLASS, belop, dato)),
            FeilregistrertV1,
        )
        val gammelInntekt = nyInntekt.convert()
        gammelInntekt?.beregnetInntekt shouldBe belop
        gammelInntekt?.endringÅrsak shouldBe Feilregistrert
        gammelInntekt?.bekreftet shouldBe true
        gammelInntekt?.manueltKorrigert shouldBe true
        val nyIM = convertToV1(lagGammelInntektsmelding(), nyID).copy(inntekt = nyInntekt)
        val konvertert = nyIM.convert()
        konvertert.naturalytelser shouldBe listOf(Naturalytelse(NaturalytelseKode.BEDRIFTSBARNEHAGEPLASS, dato, belop))
        konvertert.inntektsdato shouldBe dato
        konvertert.beregnetInntekt shouldBe belop
    }

    test("konverter reduksjon til V0") {
        val belop = 333.33
        val periode = listOf(Periode(LocalDate.EPOCH, LocalDate.MAX))
        val egenmeldinger = listOf(Periode(LocalDate.MIN, LocalDate.EPOCH))
        val nyIM = convertToV1(lagGammelInntektsmelding(), nyID).copy(
            agp = ArbeidsgiverperiodeV1(
                periode,
                egenmeldinger,
                RedusertLoennIAgpV1(belop, BegrunnelseRedusertLoennIAgpV1.FerieEllerAvspasering),
            ),
        )
        val konvertert = nyIM.convert()
        konvertert.fullLønnIArbeidsgiverPerioden?.begrunnelse shouldBe BegrunnelseIngenEllerRedusertUtbetalingKode.FerieEllerAvspasering
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalerFullLønn shouldBe false
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalt shouldBe belop
        konvertert.arbeidsgiverperioder shouldBe periode
        konvertert.egenmeldingsperioder shouldBe egenmeldinger
    }

    test("konverter null-verdi for fullLønnIAGP") {
        val orginal = lagGammelInntektsmeldingMedTommeOgNullVerdier().copy(
            forespurtData = listOf("arbeidsgiverperiode"),
            fullLønnIArbeidsgiverPerioden = null,
            arbeidsgiverperioder = lagPeriode(),
            fraværsperioder = lagPeriode(),
        )
        orginal.convertReduksjon() shouldBe null

        val nyIM = convertToV1(orginal, nyID)

        val konvertert = nyIM.convert()
        konvertert.fullLønnIArbeidsgiverPerioden?.begrunnelse shouldBe null
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalerFullLønn shouldBe true
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalt shouldBe null
    }

    test("felt som mangler i forespurt data blir ikke konvertert til v1") {
        val orginal = lagGammelInntektsmelding().copy(
            forespurtData = emptyList(),
        )
        val nyIM = convertToV1(orginal, nyID)
        nyIM.agp shouldBe null
        nyIM.inntekt shouldBe null
        nyIM.refusjon shouldBe null
    }

    test("konverter refusjon til V0") {
        val belop = 123.45
        val dato1 = LocalDate.of(2023, 2, 2)
        val dato2 = LocalDate.of(2023, 2, 2)
        val refusjon = RefusjonV1(belop, listOf(RefusjonEndringV1(belop, dato1)), dato2)
        val gammelRefusjon = refusjon.convert()
        gammelRefusjon.refusjonEndringer shouldBe listOf(RefusjonEndring(belop, dato1))
        gammelRefusjon.refusjonOpphører shouldBe dato2
        gammelRefusjon.refusjonPrMnd shouldBe belop
    }

    test("generer forespurt data") {
        val nyIM = convertToV1(lagGammelInntektsmelding(), nyID)
        nyIM.getForespurtData() shouldBe listOf("arbeidsgiverperiode", "inntekt", "refusjon")
        val utenFelter = convertToV1(lagGammelInntektsmelding(), nyID).copy(
            agp = null,
            refusjon = null,
            inntekt = null,
        )
        utenFelter.getForespurtData() shouldBe emptyList()
    }
})

fun lagPeriode(): List<Periode> {
    val start = LocalDate.of(2023, 1, 1)
    return List(3, { Periode(start.plusDays(it.toLong()), start.plusDays(it.toLong())) })
}

fun lagGammelInntektsmeldingMedTommeOgNullVerdier(): Inntektsmelding {
    return lagGammelInntektsmelding().copy(
        behandlingsdager = emptyList(),
        egenmeldingsperioder = emptyList(),
        inntektsdato = null,
        fraværsperioder = emptyList(),
        arbeidsgiverperioder = emptyList(),
        fullLønnIArbeidsgiverPerioden = null,
        naturalytelser = null,
        årsakInnsending = AarsakInnsending.ENDRING,
        innsenderNavn = null,
        telefonnummer = null,
        forespurtData = null,
    )
}

fun lagGammelInntektsmelding(): Inntektsmelding {
    return Inntektsmelding(
        orgnrUnderenhet = "123",
        identitetsnummer = "123",
        fulltNavn = "testNavn",
        virksomhetNavn = "testBedrift",
        behandlingsdager = emptyList(),
        egenmeldingsperioder = lagPeriode(),
        fraværsperioder = lagPeriode(),
        arbeidsgiverperioder = lagPeriode(),
        beregnetInntekt = 100.0,
        inntektsdato = null,
        inntekt = Inntekt(true, 100.0, null, false),
        fullLønnIArbeidsgiverPerioden = null,
        refusjon = Refusjon(true, 50.0, LocalDate.EPOCH, emptyList()),
        naturalytelser = null,
        tidspunkt = OffsetDateTime.now(),
        årsakInnsending = AarsakInnsending.NY,
        innsenderNavn = "innsender",
        telefonnummer = "22222222",
        forespurtData = listOf("arbeidsgiverperiode", "inntekt", "refusjon"),
    )
}
