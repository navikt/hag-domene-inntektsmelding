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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.deprecated.Innsending
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
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Arbeidsgiverperiode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Periode
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema.SkjemaInntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.til
import no.nav.helsearbeidsgiver.utils.test.date.februar
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.mars
import no.nav.helsearbeidsgiver.utils.test.date.oktober
import no.nav.helsearbeidsgiver.utils.test.date.september
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
        vedtaksperiodeId = UUID.randomUUID(),
    )

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

    test("convertNaturalYtelse") {
        val belop = 10.0
        val gamleYtelser = NaturalytelseKode.entries.map {
            Naturalytelse(it, foersteJanuar2023, belop)
        }.toList()
        val nyeYtelser = NaturalytelseV1.Kode.entries.map {
            NaturalytelseV1(it, belop, foersteJanuar2023)
        }.toList()
        convertNaturalYtelser(gamleYtelser) shouldBeEqual nyeYtelser
    }

    test("convertReduksjon") {
        lagGammelInntektsmelding().convertReduksjon() shouldBe null
        val utbetalt = 10000.0
        val imMedReduksjon = lagGammelInntektsmelding().copy(
            fullLønnIArbeidsgiverPerioden =
            FullLoennIArbeidsgiverPerioden(
                false,
                BegrunnelseIngenEllerRedusertUtbetalingKode.BetvilerArbeidsufoerhet,
                utbetalt,
            ),
        )
        val agp = convertToV1(imMedReduksjon, inntektsmeldingId, forespurtType).agp
        agp?.redusertLoennIAgp?.beloep shouldBe utbetalt
        agp?.redusertLoennIAgp?.begrunnelse shouldBe RedusertLoennIAgpV1.Begrunnelse.BetvilerArbeidsufoerhet
    }

    test("convertBegrunnelse") {
        val gamleBegrunnelser = BegrunnelseIngenEllerRedusertUtbetalingKode.entries.toList()
        val nyeBegrunnelser = RedusertLoennIAgpV1.Begrunnelse.entries.toList()
        gamleBegrunnelser.forEachIndexed { index, begrunnelse -> begrunnelse.convert() shouldBe nyeBegrunnelser[index] }
    }

    test("håndterer tomme lister og null-verdier") {
        val im = convertToV1(lagGammelInntektsmeldingMedTommeOgNullVerdier(), inntektsmeldingId, forespurtType)
        im.aarsakInnsending shouldBe AarsakInnsendingV1.Endring
    }

    test("konverter im til V1") {
        val gammelIM = lagGammelInntektsmelding()
        val nyIM = convertToV1(gammelIM, inntektsmeldingId, forespurtType)

        nyIM.type shouldBe forespurtType

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
        val nyIM = convertToV1(orginal, inntektsmeldingId, forespurtType)
        val gammelIM = nyIM.convert()
        gammelIM.shouldBeEqualToIgnoringFields(
            orginal,
            Inntektsmelding::inntektsdato,
            Inntektsmelding::naturalytelser,
            Inntektsmelding::fullLønnIArbeidsgiverPerioden,
            Inntektsmelding::vedtaksperiodeId,
        )
        // konvertering setter inntektsdato til epoch-tid og naturalytelse til tom liste, fullLønnIAgp som null-verdi i orginal blir oversatt til FullLoennIAGP(true, null, null)
        gammelIM.inntektsdato shouldBe LocalDate.EPOCH
        gammelIM.naturalytelser shouldBe emptyList()
        gammelIM.fullLønnIArbeidsgiverPerioden shouldBe FullLoennIArbeidsgiverPerioden(true, null, null)
        gammelIM.vedtaksperiodeId shouldBe (nyIM.type as? InntektsmeldingV1.Type.Forespurt)?.vedtaksperiodeId
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
        val nyIM = convertToV1(lagGammelInntektsmelding(), inntektsmeldingId, forespurtType).copy(inntekt = nyInntekt)
        val konvertert = nyIM.convert()
        konvertert.naturalytelser shouldBe listOf(Naturalytelse(NaturalytelseKode.BEDRIFTSBARNEHAGEPLASS, dato, belop))
        konvertert.inntektsdato shouldBe dato
        konvertert.beregnetInntekt shouldBe belop
    }

    test("konverter reduksjon til V0") {
        val belop = 333.33
        val periode = listOf(10.september til 20.september)
        val egenmeldinger = listOf(10.september til 12.september)
        val nyIM = convertToV1(lagGammelInntektsmelding(), inntektsmeldingId, forespurtType).copy(
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
        orginal.convertReduksjon() shouldBe null

        val nyIM = convertToV1(orginal, inntektsmeldingId, forespurtType)

        val konvertert = nyIM.convert()
        konvertert.fullLønnIArbeidsgiverPerioden?.begrunnelse shouldBe null
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalerFullLønn shouldBe true
        konvertert.fullLønnIArbeidsgiverPerioden?.utbetalt shouldBe null
    }

    test("felt som mangler i forespurt data blir ikke konvertert til v1") {
        val orginal = lagGammelInntektsmelding().copy(
            forespurtData = emptyList(),
        )
        val nyIM = convertToV1(orginal, inntektsmeldingId, forespurtType)
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
        val nyIM = convertToV1(lagGammelInntektsmelding(), inntektsmeldingId, forespurtType)
        nyIM.getForespurtData() shouldBe listOf("arbeidsgiverperiode", "inntekt", "refusjon")
        val utenFelter = convertToV1(lagGammelInntektsmelding(), inntektsmeldingId, forespurtType).copy(
            agp = null,
            refusjon = null,
            inntekt = null,
        )
        utenFelter.getForespurtData() shouldBe emptyList()
    }

    context("konverter SkjemaInntektsmelding til Innsending") {

        test("fullt skjema") {

            val innsending = fullInnsending().copy(
                identitetsnummer = "",
                orgnrUnderenhet = "",
            )

            fulltSkjema().convert(innsending.fraværsperioder, AarsakInnsendingV1.Endring) shouldBe innsending
        }

        test("fullt skjema uten redusert lønn i AGP") {
            val skjema = fulltSkjema().let {
                it.copy(
                    agp = it.agp?.copy(
                        redusertLoennIAgp = null,
                    ),
                )
            }

            val innsending = fullInnsending().copy(
                identitetsnummer = "",
                orgnrUnderenhet = "",
                fullLønnIArbeidsgiverPerioden = FullLoennIArbeidsgiverPerioden(
                    utbetalerFullLønn = true,
                    begrunnelse = null,
                    utbetalt = null,
                ),
            )

            skjema.convert(innsending.fraværsperioder, AarsakInnsendingV1.Endring) shouldBe innsending
        }

        test("skjema uten agp") {
            val skjema = fulltSkjema().copy(
                agp = null,
            )

            val innsending = fullInnsending().copy(
                identitetsnummer = "",
                orgnrUnderenhet = "",
                arbeidsgiverperioder = emptyList(),
                egenmeldingsperioder = emptyList(),
                fullLønnIArbeidsgiverPerioden = FullLoennIArbeidsgiverPerioden(
                    utbetalerFullLønn = true,
                    begrunnelse = null,
                    utbetalt = null,
                ),
                forespurtData = listOf(
                    "inntekt",
                    "refusjon",
                ),
            )

            skjema.convert(innsending.fraværsperioder, AarsakInnsendingV1.Endring) shouldBe innsending
        }

        test("skjema uten inntekt") {
            val skjema = fulltSkjema().copy(
                inntekt = null,
            )

            val innsending = fullInnsending().copy(
                identitetsnummer = "",
                orgnrUnderenhet = "",
                inntekt = Inntekt(
                    bekreftet = true,
                    beregnetInntekt = -1.0,
                    endringÅrsak = null,
                    manueltKorrigert = false,
                ),
                bestemmendeFraværsdag = 2.januar,
                naturalytelser = emptyList(),
                forespurtData = listOf(
                    "arbeidsgiverperiode",
                    "refusjon",
                ),
            )

            skjema.convert(innsending.fraværsperioder, AarsakInnsendingV1.Endring) shouldBe innsending
        }

        test("skjema uten refusjon") {
            val skjema = fulltSkjema().copy(
                refusjon = null,
            )

            val innsending = fullInnsending().copy(
                identitetsnummer = "",
                orgnrUnderenhet = "",
                refusjon = Refusjon(
                    utbetalerHeleEllerDeler = false,
                    refusjonPrMnd = null,
                    refusjonOpphører = null,
                    refusjonEndringer = null,
                ),
                forespurtData = listOf(
                    "arbeidsgiverperiode",
                    "inntekt",
                ),
            )

            skjema.convert(innsending.fraværsperioder, AarsakInnsendingV1.Endring) shouldBe innsending
        }
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
        orgnrUnderenhet = "123",
        identitetsnummer = "123",
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

private fun fulltSkjema(): SkjemaInntektsmelding =
    SkjemaInntektsmelding(
        avsenderTlf = "47475555",
        agp = Arbeidsgiverperiode(
            perioder = listOf(
                2.januar til 17.januar,
            ),
            egenmeldinger = listOf(
                2.januar til 2.januar,
                3.januar til 4.januar,
            ),
            redusertLoennIAgp = RedusertLoennIAgpV1(
                beloep = 10500.0,
                begrunnelse = RedusertLoennIAgpV1.Begrunnelse.FerieEllerAvspasering,
            ),
        ),
        inntekt = InntektV1(
            beloep = 21000.0,
            inntektsdato = 1.januar,
            naturalytelser = listOf(
                NaturalytelseV1(
                    naturalytelse = NaturalytelseV1.Kode.FRITRANSPORT,
                    verdiBeloep = 500.0,
                    sluttdato = 10.januar,
                ),
                NaturalytelseV1(
                    naturalytelse = NaturalytelseV1.Kode.OPSJONER,
                    verdiBeloep = 2000.0,
                    sluttdato = 20.januar,
                ),
            ),
            endringAarsak = TariffendringV1(
                gjelderFra = 1.februar,
                bleKjent = 15.februar,
            ),
        ),
        refusjon = RefusjonV1(
            beloepPerMaaned = 3000.0,
            endringer = listOf(
                RefusjonEndringV1(
                    beloep = 3500.0,
                    startdato = 15.januar,
                ),
                RefusjonEndringV1(
                    beloep = 2500.0,
                    startdato = 15.februar,
                ),
            ),
            sluttdato = 1.mars,
        ),
    )

private fun fullInnsending(): Innsending =
    Innsending(
        identitetsnummer = "03042400123",
        orgnrUnderenhet = "454989232",
        telefonnummer = "47475555",
        behandlingsdager = emptyList(),
        arbeidsgiverperioder = listOf(
            2.januar til 17.januar,
        ),
        egenmeldingsperioder = listOf(
            2.januar til 2.januar,
            3.januar til 4.januar,
        ),
        fraværsperioder = listOf(
            4.januar til 24.februar,
            3.mars til 22.mars,
        ),
        fullLønnIArbeidsgiverPerioden = FullLoennIArbeidsgiverPerioden(
            utbetalerFullLønn = false,
            begrunnelse = BegrunnelseIngenEllerRedusertUtbetalingKode.FerieEllerAvspasering,
            utbetalt = 10500.0,
        ),
        inntekt = Inntekt(
            bekreftet = true,
            beregnetInntekt = 21000.0,
            endringÅrsak = Tariffendring(
                gjelderFra = 1.februar,
                bleKjent = 15.februar,
            ),
            manueltKorrigert = true,
        ),
        bestemmendeFraværsdag = 1.januar,
        naturalytelser = listOf(
            Naturalytelse(
                naturalytelse = NaturalytelseKode.FRITRANSPORT,
                dato = 10.januar,
                beløp = 500.0,
            ),
            Naturalytelse(
                naturalytelse = NaturalytelseKode.OPSJONER,
                dato = 20.januar,
                beløp = 2000.0,
            ),
        ),
        refusjon = Refusjon(
            utbetalerHeleEllerDeler = true,
            refusjonPrMnd = 3000.0,
            refusjonOpphører = 1.mars,
            refusjonEndringer = listOf(
                RefusjonEndring(
                    beløp = 3500.0,
                    dato = 15.januar,
                ),
                RefusjonEndring(
                    beløp = 2500.0,
                    dato = 15.februar,
                ),
            ),
        ),
        forespurtData = listOf(
            "arbeidsgiverperiode",
            "inntekt",
            "refusjon",
        ),
        årsakInnsending = AarsakInnsending.ENDRING,
        bekreftOpplysninger = true,
    )
