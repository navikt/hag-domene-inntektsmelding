package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Avsender
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Kanal
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykmeldt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.TestData
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.time.OffsetDateTime
import java.util.UUID

class KonverterInnsendingTilInntektsmeldingTest :
    FunSpec({

        test("En innsending skal inneholde (nesten) nok informasjon til at man kan bygge en ferdig inntektsmelding") {
            // Denne testen er nå mest for å sjekke at formatene er kompatible,
            // at vi får sendt nok informasjon inn, og kan få tilbake nok info etter berikelse
            val eksternAvsender =
                AvsenderSystem(
                    orgnr = Orgnr.genererGyldig(),
                    navn = "TestSystem",
                    versjon = "1",
                )
            val innsending =
                Innsending(
                    innsendingId = UUID.randomUUID(),
                    skjema = TestData.fulltSkjema(),
                    aarsakInnsending = AarsakInnsending.Ny,
                    type =
                        Inntektsmelding.Type.ForespurtEkstern(
                            id = UUID.randomUUID(),
                            _avsenderSystem = eksternAvsender,
                        ),
                    innsendtTid = OffsetDateTime.now(),
                    kontaktinfo = "kontaktinformasjon",
                )
            val inntektsmelding =
                Inntektsmelding(
                    id = innsending.innsendingId,
                    type = innsending.type,
                    sykmeldt = Sykmeldt(Fnr.genererGyldig(), "Navn Navnesen"), // Fnr hentes fra fsp, navn slås opp i berik-steg
                    avsender =
                        Avsender(
                            // orgnr på bedrift kommer fra fsp
                            orgnr = Orgnr.genererGyldig(),
                            orgNavn = "TestBedrift",
                            navn = innsending.kontaktinfo,
                            tlf = innsending.skjema.avsenderTlf,
                        ),
                    sykmeldingsperioder = emptyList(), // slå opp fra fsp..
                    agp = innsending.skjema.agp,
                    inntekt = innsending.skjema.inntekt,
                    naturalytelser = innsending.skjema.naturalytelser,
                    refusjon = innsending.skjema.refusjon,
                    aarsakInnsending = innsending.aarsakInnsending,
                    mottatt = innsending.innsendtTid,
                    vedtaksperiodeId = UUID.randomUUID(), // hente fra fsp...
                )
            inntektsmelding.inntekt shouldBe innsending.skjema.inntekt
            inntektsmelding.refusjon shouldBe innsending.skjema.refusjon
            inntektsmelding.agp shouldBe innsending.skjema.agp
            inntektsmelding.id shouldBe innsending.innsendingId
            inntektsmelding.type.avsenderSystem shouldBe eksternAvsender
            inntektsmelding.type.kanal shouldBe Kanal.HR_SYSTEM_API
            inntektsmelding.avsender.navn shouldBe innsending.kontaktinfo
        }
    })
