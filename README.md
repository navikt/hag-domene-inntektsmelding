Bygg / Release

For å publisere snapshots, lag en branch som starter med dev/
Bump version i gradle.properties og inkluder -SNAPSHOT: version=1.2.3-SNAPSHOT

Github Action vil nå publisere SNAPSHOT-version ved push til branch

For å release: 
Fjern "-SNAPSHOT" fra version og merge / push til main-branch. 
