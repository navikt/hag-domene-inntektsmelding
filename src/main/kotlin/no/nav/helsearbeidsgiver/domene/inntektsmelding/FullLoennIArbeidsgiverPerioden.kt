package no.nav.helsearbeidsgiver.domene.inntektsmelding

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import no.nav.helsearbeidsgiver.utils.json.serializer.AsStringSerializer

@Serializable
data class FullLoennIArbeidsgiverPerioden(
    val utbetalerFullLønn: Boolean,
    val begrunnelse: BegrunnelseIngenEllerRedusertUtbetalingKode? = null,
    val utbetalt: Double? = null,
)

@Serializable(BegrunnelseIngenEllerRedusertUtbetalingKodeSerializer::class)
enum class BegrunnelseIngenEllerRedusertUtbetalingKode(val value: String) {
    ARBEID_OPPHOERT("ArbeidOpphoert"),
    BESKJED_GITT_FOR_SENT("BeskjedGittForSent"),
    BETVILER_ARBEIDSUFOERHET("BetvilerArbeidsufoerhet"),
    FERIE_ELLER_AVSPASERING("FerieEllerAvspasering"),
    FISKER_MED_HYRE("FiskerMedHyre"),
    FRAVAER_UTEN_GYLDIG_GRUNN("FravaerUtenGyldigGrunn"),
    IKKE_FRAVAER("IkkeFravaer"),
    IKKE_FULL_STILLINGSANDEL("IkkeFullStillingsandel"),
    IKKE_LOENN("IkkeLoenn"),
    LOVLIG_FRAVAER("LovligFravaer"),
    MANGLER_OPPTJENING("ManglerOpptjening"),
    PERMITTERING("Permittering"),
    SAERREGLER("Saerregler"),
    STREIK_ELLER_LOCKOUT("StreikEllerLockout"),
    TIDLIGERE_VIRKSOMHET("TidligereVirksomhet"),
}

internal class BegrunnelseIngenEllerRedusertUtbetalingKodeSerializer : AsStringSerializer<BegrunnelseIngenEllerRedusertUtbetalingKode>(
    serialName = "helsearbeidsgiver.kotlinx.BegrunnelseIngenEllerRedusertUtbetalingKodeSerializer",
    parse = { json ->
        BegrunnelseIngenEllerRedusertUtbetalingKode.entries.firstOrNull { begrunnelse ->
            listOf(
                begrunnelse.name,
                begrunnelse.value,
            )
                .any {
                    it.equals(json, ignoreCase = true)
                }
        }
            ?: throw SerializationException("Fant ingen begrunnelse med 'name' eller 'value' som matchet '$json'.")
    },
)
