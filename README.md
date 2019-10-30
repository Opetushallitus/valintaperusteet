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
