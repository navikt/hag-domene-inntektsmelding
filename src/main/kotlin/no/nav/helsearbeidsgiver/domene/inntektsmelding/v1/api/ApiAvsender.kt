package no.nav.helsearbeidsgiver.domene.inntektsmelding.v1.api

import kotlinx.serialization.Serializable
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr

@Serializable
data class ApiAvsender(
    val orgnr: Orgnr, // Teknisk avsender: LPS sitt Orgnr
    val avsenderSystemNavn: String,
    val avsenderSystemVersjon: String,
    val arbeidsgiverNavn: String?, // Personen som representerer sender inn - kan oppgis / fylles ut av LPS,
    // men kan være null dersom hel-automatisk prosess.
)
