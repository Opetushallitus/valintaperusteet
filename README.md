# valintaperusteet

Valintaperusteiden mallinnus sekä siihen liittyvät käyttöliittymät
ja REST-APIt.

## Ajo paikallisesti

Komennolla

``` bash
cd valintaperusteet-service
VALINTAPERUSTEET_SERVICE_USER_HOME=<polku hakemistoon oph-configuration> \
  mvn install exec:java
```

voi käynnistää valintaperusteet-palvelun paikalliseen kehitysympäristöön. Tätä varten tarvitaan
hakemistosta `src/main/resources/oph-configuration` löytyvien pohjien mukaan muodostetut
asetukset.

Toinen vaihtoehto on käyttää luokkaa [ValintaperusteetJettyForLocalDev](valintaperusteet-testing/src/main/java/fi/vm/sade/service/valintaperusteet/ValintaperusteetJettyForLocalDev.java)
suoraan IDEstä.

## Testien ajaminen

``mvn package``

Jos haluat ajaa tietokantaa käyttäviä testejä erikseen IDEstä, käynnistä PostgreSQL niitä varten:

``mvn -pl valintaperusteet-service/pom.xml -Dtestikannan.portti=4320 docker:start``

Testi-PostgreSQL:n sammuttaminen hoituu samaan tapaan `mvn -pl valintaperusteet-service/pom.xml docker:stop`.

Huom: pelkkä `mvn test` jättää testikannan käyntiin. Se näkyy `docker ps`:llä.
