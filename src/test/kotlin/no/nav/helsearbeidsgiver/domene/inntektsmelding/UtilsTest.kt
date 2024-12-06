@file:Suppress("DEPRECATION")

package no.nav.helsearbeidsgiver.domene.inntektsmelding

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convert
import no.nav.helsearbeidsgiver.domene.inntektsmelding.Utils.convertInntekt
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.oktober
import no.nav.helsearbeidsgiver.utils.test.date.september
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending as AarsakInnsendingV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode as ArbeidsgiverperiodeV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Bonus as BonusV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Feilregistrert as FeilregistrertV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferie as FerieV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Ferietrekk as FerietrekkV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntekt as InntektV1
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Tariffendring as TariffendringV1
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.VarigLoennsendring as VarigLoennsendringV1

class UtilsTest : FunSpec({

    val foersteJanuar2023 = LocalDate.of(2023, 1, 1)
    val inntektsmeldingId = UUID.randomUUID()
    val forespurtType = InntektsmeldingV1.Type.Forespurt(
        id = UUID.randomUUID(),
    )

    test("convertInntekt") {
        val im = lagGammelInntektsmelding()
        val nyInntekt = im.convertInntekt()
        nyInntekt shouldNotBe null
        val gammelInntekt = im.inntekt
        nyInntekt.beloep shouldBe gammelInntekt?.beregnetInntekt
    }

    test("convertInntekt kaster exception hvis null") {
        val im = lagGammelInntektsmelding().copy(inntekt = null)
        shouldThrowExactly<IllegalArgumentException> {
            im.convertInntekt()
        }
    }

    test("convertEndringAarsak") {
        Bonus(1.0, LocalDate.EPOCH).convert() shouldBe BonusV1
        Bonus(null, null).convert() shouldBe BonusV1
        Feilregistrert.convert() shouldBe FeilregistrertV1
        Ferie(lagPeriode()).convert() shouldBe FerieV1(lagPeriode())
        Ferietrekk.convert() shouldBe FerietrekkV1
        Nyansatt.convert() shouldBe NyansattV1
        NyStilling(foersteJanuar2023).convert() shouldBe NyStillingV1(foersteJanuar2023)
        NyStillingsprosent(foersteJanuar2023).convert() shouldBe NyStillingsprosentV1(foersteJanuar2023)
        Permisjon(lagPeriode()).convert() shouldBe PermisjonV1(lagPeriode())
        Permittering(lagPeriode()).convert() shouldBe PermitteringV1(lagPeriode())
        Sykefravaer(lagPeriode()).convert() shouldBe SykefravaerV1(lagPeriode())
        Tariffendring(foersteJanuar2023, foersteJanuar2023).convert() shouldBe TariffendringV1(
            foersteJanuar2023,
            foersteJanuar2023,
        )
        VarigLonnsendring(foersteJanuar2023).convert() shouldBe VarigLoennsendringV1(foersteJanuar2023)
    }

    test("Naturalytelse-convert") {
        val belop = 10.0
        val gamleYtelser = NaturalytelseKode.entries.map {
            Naturalytelse(it, foersteJanuar2023, belop)
        }.toList()
        val nyeYtelser = NaturalytelseV1.Kode.entries.map {
            NaturalytelseV1(it, belop, foersteJanuar2023)
        }.toList()
        gamleYtelser.map { it.convert() } shouldBeEqual nyeYtelser
    }

    test("FullLoennIArbeidsgiverPerioden-convert") {
        val utbetalt = 10000.0
        val imMedReduksjon = lagGammelInntektsmelding().copy(
            fullLønnIArbeidsgiverPerioden =
            FullLoennIArbeidsgiverPerioden(
                false,
                BegrunnelseIngenEllerRedusertUtbetalingKode.BetvilerArbeidsufoerhet,
                utbetalt,
            ),
        )
        val agp = imMedReduksjon.convertToV1(inntektsmeldingId, forespurtType).agp
        agp?.redusertLoennIAgp?.beloep shouldBe utbetalt
        agp?.redusertLoennIAgp?.begrunnelse shouldBe RedusertLoennIAgpV1.Begrunnelse.BetvilerArbeidsufoerhet
    }

    test("convertBegrunnelse") {
        val gamleBegrunnelser = BegrunnelseIngenEllerRedusertUtbetalingKode.entries.toList()
        val nyeBegrunnelser = RedusertLoennIAgpV1.Begrunnelse.entries.toList()
        gamleBegrunnelser.forEachIndexed { index, begrunnelse -> begrunnelse.convert() shouldBe nyeBegrunnelser[index] }
    }

    test("håndterer tomme lister og null-verdier") {
        val im = lagGammelInntektsmeldingMedTommeOgNullVerdier().convertToV1(inntektsmeldingId, forespurtType)
        im.aarsakInnsending shouldBe AarsakInnsendingV1.Endring
    }

    test("konverter im til V1") {
        val gammelIM = lagGammelInntektsmelding()
        val nyIM = gammelIM.convertToV1(inntektsmeldingId, forespurtType)

        nyIM.type shouldBe forespurtType

        nyIM.sykmeldt.fnr.verdi shouldBe gammelIM.identitetsnummer
        nyIM.sykmeldt.navn shouldBe gammelIM.fulltNavn

        nyIM.avsender.navn shouldBe gammelIM.innsenderNavn
        nyIM.avsender.orgNavn shouldBe gammelIM.virksomhetNavn
        nyIM.avsender.orgnr.verdi shouldBe gammelIM.orgnrUnderenhet
        nyIM.avsender.tlf shouldBe gammelIM.telefonnummer

        nyIM.aarsakInnsending shouldBe AarsakInnsendingV1.Ny
    }

    test("konverter fra nytt til gammelt IM-format") {
        val orginal = lagGammelInntektsmelding()
        val nyIM = orginal.convertToV1(inntektsmeldingId, forespurtType)
        val gammelIM = nyIM.convert()
        gammelIM.shouldBeEqualToIgnoringFields(
            orginal,
            Inntektsmelding::inntektsdato,
            Inntektsmelding::naturalytelser,
            Inntektsmelding::fullLønnIArbeidsgiverPerioden,
            Inntektsmelding::vedtaksperiodeId,
        )
        // konvertering setter inntektsdato til orginal.bestemmendeFraværsdag hvis orginal.inntektsdato er null.
        // naturalytelse settes til tom liste
        // fullLønnIAgp som null-verdi i orginal blir oversatt til FullLoennIAGP(true, null, null)
        gammelIM.inntektsdato shouldBe orginal.bestemmendeFraværsdag
        gammelIM.naturalytelser shouldBe emptyList()
        gammelIM.fullLønnIArbeidsgiverPerioden shouldBe FullLoennIArbeidsgiverPerioden(true, null, null)
        gammelIM.vedtaksperiodeId shouldBe nyIM.vedtaksperiodeId
    }

    test("konverter inntekt fra nytt til gammelt IM-format") {
        val belop = 1000.0
        val dato = LocalDate.of(2024, 1, 1)
        val nyInntekt = InntektV1(
            belop,
            dato,
            listOf(NaturalytelseV1(NaturalytelseV1.Kode.BEDRIFTSBARNEHAGEPLASS, belop, dato)),
            FeilregistrertV1,
        )
        val gammelInntekt = nyInntekt.convert()
        gammelInntekt.beregnetInntekt shouldBe belop
        gammelInntekt.endringÅrsak shouldBe Feilregistrert
        gammelInntekt.bekreftet shouldBe true
        gammelInntekt.manueltKorrigert shouldBe true
        val nyIM = lagGammelInntektsmelding().convertToV1(inntektsmeldingId, forespurtType).copy(inntekt = nyInntekt)
        val konvertert = nyIM.convert()
        konvertert.naturalytelser shouldBe listOf(Naturalytelse(NaturalytelseKode.BEDRIFTSBARNEHAGEPLASS, dato, belop))
        konvertert.inntektsdato shouldBe dato
        konvertert.beregnetInntekt shouldBe belop
    }

    test("konverter reduksjon til V0") {
        val belop = 333.33
        val periode = listOf(10.september til 20.september)
        val egenmeldinger = listOf(10.september til 12.september)
        val nyIM = lagGammelInntektsmelding().convertToV1(inntektsmeldingId, forespurtType).copy(
            agp = ArbeidsgiverperiodeV1(
                periode,
                egenmeldinger,
                RedusertLoennIAgpV1(belop, RedusertLoennIAgpV1.Begrunnelse.FerieEllerAvspasering),
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
            fullLønnIArbeidsgiverPerioden = null,
        )

        val nyIM = orginal.convertToV1(inntektsmeldingId, forespurtType)

        val konvertert = nyIM.convert()
        konvertert.fullLønnIArbeidsgiverPerioden?.begrunnelse shouldBe null
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalerFullLønn shouldBe true
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalt shouldBe null
    }

    test("felt som mangler i forespurt data blir ikke konvertert til v1") {
        val orginal = lagGammelInntektsmelding().copy(
            forespurtData = emptyList(),
        )
        val nyIM = orginal.convertToV1(inntektsmeldingId, forespurtType)
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
        val nyIM = lagGammelInntektsmelding().convertToV1(inntektsmeldingId, forespurtType)
        nyIM.getForespurtData() shouldBe listOf("arbeidsgiverperiode", "inntekt", "refusjon")
        val utenFelter = lagGammelInntektsmelding().convertToV1(inntektsmeldingId, forespurtType).copy(
            agp = null,
            refusjon = null,
            inntekt = null,
        )
        utenFelter.getForespurtData() shouldBe emptyList()
    }
})

fun lagPeriode(): List<Periode> {
    val start = LocalDate.of(2023, 1, 1)
    return List(3) { Periode(start.plusDays(it.toLong()), start.plusDays(it.toLong())) }
}

fun lagGammelInntektsmeldingMedTommeOgNullVerdier(): Inntektsmelding {
    return lagGammelInntektsmelding().copy(
        behandlingsdager = emptyList(),
        egenmeldingsperioder = emptyList(),
        inntektsdato = null,
        fraværsperioder = listOf(1.januar til 1.januar),
        arbeidsgiverperioder = emptyList(),
        fullLønnIArbeidsgiverPerioden = null,
        naturalytelser = null,
        årsakInnsending = AarsakInnsending.ENDRING,
        innsenderNavn = null,
        telefonnummer = null,
        forespurtData = null,
    )
}

private fun lagGammelInntektsmelding(): Inntektsmelding =
    Inntektsmelding(
        orgnrUnderenhet = Orgnr.genererGyldig().verdi,
        identitetsnummer = Fnr.genererGyldig().verdi,
        fulltNavn = "testNavn",
        virksomhetNavn = "testBedrift",
        behandlingsdager = emptyList(),
        egenmeldingsperioder = listOf(
            12.september til 13.september,
        ),
        fraværsperioder = listOf(
            14.september til 20.september,
            28.september til 21.oktober,
        ),
        arbeidsgiverperioder = listOf(
            12.september til 20.september,
            28.september til 4.oktober,
        ),
        beregnetInntekt = 100.0,
        inntektsdato = null,
        inntekt = Inntekt(
            bekreftet = true,
            beregnetInntekt = 100.0,
            endringÅrsak = null,
            manueltKorrigert = false,
        ),
        fullLønnIArbeidsgiverPerioden = null,
        refusjon = Refusjon(
            utbetalerHeleEllerDeler = true,
            refusjonPrMnd = 50.0,
            refusjonOpphører = LocalDate.EPOCH,
            refusjonEndringer = emptyList(),
        ),
        naturalytelser = null,
        tidspunkt = OffsetDateTime.now(),
        årsakInnsending = AarsakInnsending.NY,
        innsenderNavn = "innsender",
        telefonnummer = "22222222",
        forespurtData = listOf("arbeidsgiverperiode", "inntekt", "refusjon"),
        vedtaksperiodeId = UUID.randomUUID(),
    )
