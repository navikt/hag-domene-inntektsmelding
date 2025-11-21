package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1

import no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.skjema.SkjemaInntektsmelding
import no.nav.helsearbeidsgiver.utils.test.date.juli
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import java.util.UUID

object TestData {
    fun fulltSkjema(): SkjemaInntektsmelding =
        SkjemaInntektsmelding(
            forespoerselId = UUID.randomUUID(),
            avsenderTlf = "45456060",
            agp =
                Arbeidsgiverperiode(
                    perioder =
                        listOf(
                            2.juni til 2.juni,
                            4.juni til 18.juni,
                        ),
                    egenmeldinger =
                        listOf(
                            2.juni til 2.juni,
                            4.juni til 5.juni,
                        ),
                    redusertLoennIAgp =
                        RedusertLoennIAgp(
                            beloep = 34000.0,
                            begrunnelse = RedusertLoennIAgp.Begrunnelse.LovligFravaer,
                        ),
                ),
            inntekt =
                Inntekt(
                    beloep = 50000.0,
                    inntektsdato = 31.mai,
                    endringAarsaker =
                        listOf(
                            Tariffendring(
                                gjelderFra = 30.juni,
                                bleKjent = 5.juli,
                            ),
                        ),
                ),
            naturalytelser =
                listOf(
                    Naturalytelse(
                        naturalytelse = Naturalytelse.Kode.OPSJONER,
                        verdiBeloep = 4000.0,
                        sluttdato = 15.juni,
                    ),
                    Naturalytelse(
                        naturalytelse = Naturalytelse.Kode.ELEKTRONISKKOMMUNIKASJON,
                        verdiBeloep = 555.0,
                        sluttdato = 25.juni,
                    ),
                ),
            refusjon =
                Refusjon(
                    beloepPerMaaned = 10000.0,
                    endringer =
                        listOf(
                            RefusjonEndring(
                                beloep = 8000.0,
                                startdato = 10.juli,
                            ),
                            RefusjonEndring(
                                beloep = 6000.0,
                                startdato = 20.juli,
                            ),
                        ),
                ),
        )
}
