# ovara-valintaperusteet #

Erillinen moduuli siirtotiedostojen ajastetulle luomiselle. Main-luokka OvaraApp etsii käynnistyessään
sovelluksen kannasta viimeisimmän onnistuneen siirtotiedostojen muodostuksen aikaikkunan loppuhetken.
Uusi aikaikkuna määritellään operaation alkaessa edellisen onnistuneen aikaikkunan lopusta nykyhetkeen.

Jos muuttuneita tietoja on aikavälillä paljon (kts. ympäristökonffeissa arvo valintaperusteet_siirtotiedosto_max_hakukohde_count_in_file), muodostuu useita tiedostoja.

Muodostetut tiedostot tallennetaan sovellukselle konffattuun s3-ämpäriin seuraavien konffiarvojen perusteella:
suoritusrekisteri.ovara.s3.region
suoritusrekisteri.ovara.s3.bucket
suoritusrekisteri.ovara.s3.target-role-arn

Sovelluksen ajoympäristö kts. cloud-base -> ovara-generic-stack.ts.

## Ajoympäristöjen versiot

- Java 17

## Ajaminen lokaalisti

Sovelluksen lokaali ajaminen vaatii sopivan kannan tai putkituksen sellaiseen, tarvittaessa ovara-valintaperusteet/src/main/resources/application.yml kanssa yhteensopivan tyhjän kannan saa pystyyn tähän tapaan:
``docker run --rm --name valintaperusteet-db -p 4320:5432 -e POSTGRES_USER=valintaperusteet_tests -e POSTGRES_PASSWORD=testikannan_salasana -e POSTGRES_DB=valintaperusteet_tests -d postgres:11.5``

Käynnistetään ajamalla OvaraApp-luokka. Tämän voi tehdä joko IDEstä (katso alta tarvittavat konffi- ja profiiliparametrit kuntoon)
tai projektin juuresta suoraan ovara-valintaperusteiden spring boot-jaria suoraan esimerkiksi näin:
``mvn clean install``
``java -Dspring.config.additional-location=ovara-valintaperusteet/src/main/resources/application.yml -Dspring.profiles.active=ovara -jar ovara-valintaperusteet/target/ovara-valintaperusteet.jar``

 