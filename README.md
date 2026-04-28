# LaBoulangerieCore

Plugin Paper 1.21+ pour le serveur La Boulangerie.

## Features

### Reduction de pousse des cultures
Les cultures poussent plus lentement sans acces direct au ciel.
```yaml
crop-growth:
  enabled: true
  min-skylight-level: 12
  no-skylight-rate: 0.1
```

### Calendrier personnalise
1 semaine reelle = 1 annee in-game.
Placeholders PAPI: `%lbcore_date_day%`, `%lbcore_date_year%`, `%lbcore_date_full%`

### Crafts desactives
Liste configurable de crafts bloques (armor trims, etc.)

### Tab personnalise
Header, footer et nom configurables avec MiniMessage + PAPI.

## Build

```bash
./gradlew build
```

Le JAR est dans `build/libs/`.
