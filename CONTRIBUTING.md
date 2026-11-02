# Contributing – Wishlist4life

Retningslinjer for hvordan vi arbejder sammen på projektet. Læs dem før du laver din første PR.

## Workflow

1. Tag et kort fra **Sprint Backlog** på [Wishlist Scrum Board](https://github.com/orgs/Wishlist4life/projects/2), assign dig selv og flyt det til **In Progress**.
2. Opret en branch fra `main` (se navngivning nedenfor).
3. Lav dine ændringer i små commits.
4. Push og åbn en PR til `main`. Flyt kortet til **Review / Testing**.
5. Når PR'en er godkendt og CI er grøn, merges den. Kortet flyttes til **Done**.

## Branch-navngivning

| Type | Format | Eksempel |
|------|--------|----------|
| Feature | `feature/US<nr>-kort-beskrivelse` | `feature/US3-opret-oenskeseddel` |
| Bugfix | `fix/kort-beskrivelse` | `fix/login-redirect` |
| Dokumentation | `docs/kort-beskrivelse` | `docs/contributing` |
| CI/CD | `ci/kort-beskrivelse` | `ci/qodana` |

Brug små bogstaver og bindestreger. Undgå æ, ø og å i branch-navne.

## Commits

- Skriv korte beskeder i bydeform: `Add WishlistRepository`, ikke `Added stuff`.
- Henvis til issue-nummeret: `Add WishlistRepository #52`.
- Én logisk ændring pr. commit.

## Pull requests

- PR'er går altid til `main` – der kan ikke pushes direkte til `main`.
- Kræver **1 approval** fra et andet gruppemedlem.
- CI skal være grøn: **build-test** (build + tests) og **qodana** (kodeanalyse).
- Skriv `Closes #<nr>` i beskrivelsen, så issuet lukkes automatisk ved merge.
- Hold PR'er små – helst én task pr. PR.
- Reviewer: tjek at koden virker, er testet og følger strukturen nedenfor.
- Tilføjet en DEVELOPMENT BRANCH før `main`

## Kodestruktur

Vi bygger hver feature i lag:

1. **Model** – Java-klassen + tabel i `schema.sql`
2. **Repository** – databaseadgang (JDBC)
3. **Service** – forretningslogik og validering
4. **Controller** – request-håndtering + Thymeleaf-side i `templates/`

Controllere kalder services, services kalder repositories – aldrig på tværs af lag.

## Tests

| Lag | Testtype | Værktøj |
|-----|----------|---------|
| Repository | Integrationstest | Spring Boot Test + H2 |
| Service | Unittest | JUnit 5 + Mockito |
| Controller | (valgfrit) | MockMvc |

Kør alle tests lokalt før du pusher:

```bash
mvn verify -f demo/pom.xml
```

## Definition of Done

En task/story er færdig når:

- [ ] Acceptkriterierne er opfyldt
- [ ] Koden følger lagstrukturen
- [ ] Tests er skrevet og grønne
- [ ] CI er grøn (build, tests, Qodana)
- [ ] PR er godkendt af mindst ét gruppemedlem
- [ ] Merget til `main` og deployet på Azure
- [ ] Kortet er flyttet til **Done**
