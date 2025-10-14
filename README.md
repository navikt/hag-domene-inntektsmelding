# Helse-Arbeidsgiver sine domeneobjekter for inntektsmelding

En samling av domeneobjekter som brukes internt i systemene til Helse-Arbeidsgiver.

## Komme i gang

Hovedobjektene er `Inntektsmelding`, `Innsending` og `SkjemaInntektsmelding`.

### Publisere nye versjoner

For å publisere snapshots, push til en branch som starter med `dev/`.
Snapshot-versjonen er basert på `version` i `gradle.properties`. Ved `version=1.2.3` så vil workflow publisere en snapshot `1.2.3-SNAPSHOT`.
Snapshot-versjoner overskrives for hvert push.

For å publisere ny versjon, oppdater `version` i `gradle.properties` og push til branch `main`.
Dersom versjon allerede eksisterer så vil workflow feile med `409 Conflict`.

---

## Henvendelser

Spørsmål knyttet til koden eller repositoryet kan stilles som [issues](https://github.com/navikt/hag-domene-inntektsmelding/issues/new) her på GitHub.

### For Nav-ansatte

Interne henvendelser kan sendes via Slack i kanalen [#helse-arbeidsgiver](https://nav-it.slack.com/archives/CSMN6320N).
