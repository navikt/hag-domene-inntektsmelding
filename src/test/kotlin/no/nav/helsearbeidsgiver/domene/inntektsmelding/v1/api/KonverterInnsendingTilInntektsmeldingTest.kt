package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.AarsakInnsending
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Avsender
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Inntektsmelding
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Kanal
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.Sykmeldt
import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.TestFactory
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.time.OffsetDateTime
import java.util.UUID

class KonverterInnsendingTilInntektsmeldingTest : FunSpec({

    test("En innsending skal inneholde (nesten) nok informasjon til at man kan generere en ferdig inntektsmelding") {
        // Denne testen er nå mest for å sjekke at formatene er kompatible,
        // at vi får sendt nok informasjon inn, og kan få tilbake nok info etter berikelse
        val innsending = Innsending(
            UUID.randomUUID(),
            TestFactory.fulltSkjema(),
            AarsakInnsending.Ny,
            Inntektsmelding.Type.Forespurt(UUID.randomUUID()),
            ApiAvsender(Orgnr.genererGyldig(), "TestSystem", "1", "Arve Arbeidsgiver"),
            OffsetDateTime.now(),
        )
        val inntektsmelding = Inntektsmelding(
            id = innsending.innsendingId,
            type = innsending.type,
            sykmeldt = Sykmeldt(Fnr.genererGyldig(), "Navn Navnesen"), // Fnr hentes fra fsp, navn slås opp i berik-steg
            avsender = Avsender(Orgnr.genererGyldig(), "TestBedrift", "En Ansatt", innsending.skjema.avsenderTlf), // orgnr på bedrift kommer fra fsp,
            sykmeldingsperioder = emptyList(), // slå opp fra fsp..
            agp = innsending.skjema.agp,
            inntekt = innsending.skjema.inntekt,
            refusjon = innsending.skjema.refusjon,
            aarsakInnsending = innsending.aarsakInnsending,
            mottatt = innsending.innsendtTid,
            vedtaksperiodeId = UUID.randomUUID(), // hente fra fsp...
            kanal = Kanal.NAV_API,
        )
        inntektsmelding.inntekt shouldBe innsending.skjema.inntekt
        inntektsmelding.refusjon shouldBe innsending.skjema.refusjon
        inntektsmelding.agp shouldBe innsending.skjema.agp
        inntektsmelding.id shouldBe innsending.innsendingId
    }
})
