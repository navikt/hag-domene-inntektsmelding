package no.nav.helsearbeidsgiver.domene.inntektsmelding

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertBegrunnelse
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertEndringAarsak
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertInntekt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertInntektToV0
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertNaturalYtelser
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertReduksjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertRefusjonToV0
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertToV0
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertToV1
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NyStillingsprosent
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Nyansatt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Permisjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Permittering
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Refusjon
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Sykefravaer
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Tariffendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.VarigLonnsendring
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.BegrunnelseRedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStilling
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RedusertLoennIAgp
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.RefusjonEndring
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.OffsetDateTime

class UtilsTest : FunSpec({

    val dato = LocalDate.of(2023, 1, 1)

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
        val bonus = Bonus(1.0, LocalDate.EPOCH)
        convertEndringAarsak(bonus) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus
        convertEndringAarsak(Bonus(null, null)) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus
        convertEndringAarsak(Feilregistrert) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert
        convertEndringAarsak(Ferie(lagPeriode())) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie(lagPeriode())
        convertEndringAarsak(Ferietrekk) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk
        convertEndringAarsak(Nyansatt) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Nyansatt
        convertEndringAarsak(no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.NyStilling(dato)) shouldBe NyStilling(dato)
        convertEndringAarsak(NyStillingsprosent(dato)) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NyStillingsprosent(dato)
        convertEndringAarsak(Permisjon(lagPeriode())) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permisjon(lagPeriode())
        convertEndringAarsak(Permittering(lagPeriode())) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Permittering(
            lagPeriode(),
        )
        convertEndringAarsak(Sykefravaer(lagPeriode())) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykefravaer(
            lagPeriode(),
        )
        convertEndringAarsak(Tariffendring(dato, dato)) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring(
            dato, dato,
        )
        convertEndringAarsak(VarigLonnsendring(dato)) shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring(
            dato,
        )
    }

    test("convertNaturalYtelse") {
        val belop = 10.0
        val gamleYtelser = NaturalytelseKode.entries.map {
            Naturalytelse(it, dato, belop)
        }.toList()
        val nyeYtelser = no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NaturalytelseKode.entries.map {
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse(it, belop, dato)
        }.toList()
        convertNaturalYtelser(gamleYtelser) shouldBeEqual nyeYtelser
    }

    test("convertReduksjon") {
        convertReduksjon(lagGammelInntektsmelding()) shouldBe null
        val utbetalt = 10000.0
        val im_med_reduksjon = lagGammelInntektsmelding().copy(
            fullLønnIArbeidsgiverPerioden =
            FullLoennIArbeidsgiverPerioden(false, BegrunnelseIngenEllerRedusertUtbetalingKode.BetvilerArbeidsufoerhet, utbetalt),
        )
        val agp = convertToV1(im_med_reduksjon).agp
        agp?.redusertLoennIAgp?.beloep shouldBe utbetalt
        agp?.redusertLoennIAgp?.begrunnelse shouldBe BegrunnelseRedusertLoennIAgp.BetvilerArbeidsufoerhet
    }

    test("convertBegrunnelse") {
        val gamleBegrunnelser = BegrunnelseIngenEllerRedusertUtbetalingKode.entries.toList()
        val nyeBegrunnelser = BegrunnelseRedusertLoennIAgp.entries.toList()
        gamleBegrunnelser.forEachIndexed { index, begrunnelse -> convertBegrunnelse(begrunnelse) shouldBe nyeBegrunnelser[index] }
    }

    test("håndterer tomme lister og null-verdier") {
        val im = convertToV1(lagGammelInntektsmeldingMedTommeOgNullVerdier())
        im.aarsakInnsending shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending.Endring
    }

    test("konverter im til V1") {
        val gammelIM = lagGammelInntektsmelding()
        val nyIM = convertToV1(gammelIM)
        nyIM.sykmeldt.fnr shouldBe gammelIM.identitetsnummer
        nyIM.sykmeldt.navn shouldBe gammelIM.fulltNavn

        nyIM.avsender.navn shouldBe gammelIM.innsenderNavn
        nyIM.avsender.orgNavn shouldBe gammelIM.virksomhetNavn
        nyIM.avsender.orgnr shouldBe gammelIM.orgnrUnderenhet
        nyIM.avsender.tlf shouldBe gammelIM.telefonnummer

        nyIM.aarsakInnsending shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending.Ny
    }

    test("konverter fra nytt til gammelt IM-format") {
        val orginal = lagGammelInntektsmelding()
        val nyIM = convertToV1(orginal)
        val gammelIM = convertToV0(nyIM)
        gammelIM.shouldBeEqualToIgnoringFields(orginal, Inntektsmelding::inntektsdato, Inntektsmelding::naturalytelser)
        // konvertering setter inntektsdato til epoch-tid og naturalytelse til tom liste
    }

    test("konverter inntekt fra nytt til gammelt IM-format") {
        val belop = 1000.0
        val dato = LocalDate.of(2024, 1, 1)
        val nyInntekt = no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt(
            belop,
            dato,
            listOf(no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Naturalytelse(no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.NaturalytelseKode.BEDRIFTSBARNEHAGEPLASS, belop, dato)),
            no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert,
        )
        val gammelInntekt = convertInntektToV0(nyInntekt)
        gammelInntekt?.beregnetInntekt shouldBe belop
        gammelInntekt?.endringÅrsak shouldBe no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Feilregistrert
        gammelInntekt?.bekreftet shouldBe true
        gammelInntekt?.manueltKorrigert shouldBe true
        val nyIM = convertToV1(lagGammelInntektsmelding()).copy(inntekt = nyInntekt)
        val konvertert = convertToV0(nyIM)
        konvertert.naturalytelser shouldBe listOf(Naturalytelse(NaturalytelseKode.BEDRIFTSBARNEHAGEPLASS, dato, belop))
        konvertert.inntektsdato shouldBe dato
        konvertert.beregnetInntekt shouldBe belop
    }

    test("konverter reduksjon til V0") {
        val belop = 333.33
        val periode = listOf(Periode(LocalDate.EPOCH, LocalDate.MAX))
        val nyIM = convertToV1(lagGammelInntektsmelding()).copy(
            agp = Arbeidsgiverperiode(
                periode,
                periode,
                RedusertLoennIAgp(belop, BegrunnelseRedusertLoennIAgp.FerieEllerAvspasering),
            ),
        )
        val konvertert = convertToV0(nyIM)
        konvertert.fullLønnIArbeidsgiverPerioden?.begrunnelse shouldBe BegrunnelseIngenEllerRedusertUtbetalingKode.FerieEllerAvspasering
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalerFullLønn shouldBe false
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalt shouldBe belop
        konvertert.arbeidsgiverperioder shouldBe periode
        konvertert.egenmeldingsperioder shouldBe periode
    }

    test("konverter refusjon til V0") {
        val belop = 123.45
        val dato1 = LocalDate.of(2023, 2, 2)
        val dato2 = LocalDate.of(2023, 2, 2)
        val refusjon = no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Refusjon(belop, listOf(RefusjonEndring(belop, dato1)), dato2)
        val gammelRefusjon = convertRefusjonToV0(refusjon)
        gammelRefusjon.refusjonEndringer shouldBe listOf(no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.RefusjonEndring(belop, dato1))
        gammelRefusjon.refusjonOpphører shouldBe dato2
        gammelRefusjon.refusjonPrMnd shouldBe belop
    }
})

fun lagPeriode(): List<Periode> {
    val start = LocalDate.of(2023, 1, 1)
    return List(3, { Periode(start.plusDays(it.toLong()), start.plusDays(it.toLong())) })
}

fun lagGammelInntektsmeldingMedTommeOgNullVerdier(): Inntektsmelding {
    return lagGammelInntektsmelding().copy(
        behandlingsdager = emptyList(), egenmeldingsperioder = emptyList(),
        inntektsdato = null, fraværsperioder = emptyList(), arbeidsgiverperioder = emptyList(), fullLønnIArbeidsgiverPerioden = null,
        naturalytelser = null, årsakInnsending = AarsakInnsending.ENDRING, innsenderNavn = null, telefonnummer = null, forespurtData = null,
    )
}

fun lagGammelInntektsmelding(): Inntektsmelding {
    val dato = LocalDate.of(2023, 1, 1)
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
        refusjon = Refusjon(true),
        naturalytelser = null,
        tidspunkt = OffsetDateTime.now(),
        årsakInnsending = AarsakInnsending.NY,
        innsenderNavn = "innsender",
        telefonnummer = "22222222",
    )
}
