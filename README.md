# Wishlist – Digital ønskeseddel
Udarbejdet af Nicklas, Mikkel, Valdemar & Jeppe

Webapplikation hvor brugere kan oprette ønskesedler, dele dem med andre og reservere ønsker.

**Live** https://wishlist4life-d5dqh3cwh8bjaxap.swedencentral-01.azurewebsites.net

## Features
- Opret bruger og log ind
- Opret og redigér ønskesedler
- Del ønskeseddel via link
- Reservér ønsker på andres ønskesedler

## Tech stack
Java 21 · Spring Boot · Thymeleaf · MySQL · Azure App Service · GitHub Actions · Qodana

## Forudsætninger
**For at bruge applikationen:**
- En opdateret webbrowser (Chrome, Firefox, Safari eller Edge)
- Internetforbindelse

**For at udvikle på applikationen:**
- JDK 21
- Maven
- MySQL
- IntelliJ IDEA

## Database
SQL-scripts ligger i `src/main/resources`:
- `schema.sql` – opretter tabellerne (`users`, `wishlist`, `wish`, `reservation`)
- `data.sql` – indsætter testdata (login: `mia@test.dk` / `Test1234`). Kør den kun én gang mod MySQL.

Mod Azure MySQL køres begge scripts manuelt i databasen `wishlist`. I tests (H2) køres de automatisk.

Databasen kører på Azure. Forbindelsesoplysninger håndteres via miljøvariabler i Azure og GitHub Secrets.

## Tests
Unittests og integrationstests køres automatisk i GitHub Actions ved hver pull request til `main`.
Integrationstests kører mod en H2 in-memory database.

## CI/CD
- **Pull request til `main`:** build, unit- og integrationstests samt Qodana-analyse
- **Merge til `main`:** automatisk deploy til Azure App Service (Sweden Central)
- `main` er beskyttet – alle ændringer skal via PR med mindst én godkendelse

## Dokumentation
- ER-model
- Klassediagram
- UX/UI (ideation, wireframes, usability test, heuristisk evaluering)
- Qodana-rapport

## Team
| Navn | GitHub |
|------|--------|
| Nicklas Clausen | [@ClausenEK](https://github.com/ClausenEK) |
| Mikkel | [@ChefenML](https://github.com/ChefenML) |
| Valdemar | [@ValdemarTTE](https://github.com/ValdemarTTE) |
| Jeppe | [@Jeppeoberg](https://github.com/Jeppeoberg) |

## Bidrag
Se [CONTRIBUTING.md](CONTRIBUTING.md).
