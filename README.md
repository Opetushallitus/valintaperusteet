# valintaperusteet

Valintaperusteiden mallinnus sekä siihen liittyvät käyttöliittymät
ja REST-APIt.

## Ajo paikallisesti

Komennoilla

``` bash
mvn install
mvn -pl valintaperusteet-service/pom.xml -Dtestikannan.portti=4320 docker:start
cd valintaperusteet-service
mvn -DskipTests -Dtestikannan.portti=4320 exec:java
```

voi käynnistää valintaperusteet-palvelun paikalliseen kehitysympäristöön.

## Koodin tyyli ja Git pre-commit hook

Koodin tyyli tarkistetaan spotless-työkalulla. Tarkistuksen voi ajaa manuaalisesti kaikelle koodille komennolla:

```
mvn spotless:check
```

Koodin tyylin automaattiseen tarkistukseen on otettu käyttöön Git pre-commit hook, joka pyrkii korjaamaan tyylivirheet ennen committia.
Pre-commit hook on tehty käyttäen [pre-commit](https://pre-commit.com/)-työkalua, joka on toteutettu Python-ohjelmointikielellä.
Python on yleensä jo valmiiksi asennettuna muita työkaluja varten.

Asenna pre-commit-riippuvuus pip:llä:
```
pip install pre-commit
```

Varsinainen koodin korjaava Git hook asennetaan mavenilla initialize-elinkaaritapahtuman yhteydessä.
Hook asentuu esimerkiksi ajamalla Mavenilla komennon:
```
mvn initialize
```

Jos hookin asentaminen epäonnistuu, maven-komento palauttaa virheen ja ohjaa asentamaan pre-commit-riippuvuuden pip:llä. 
Onnistunut hookin asennus tulostaa logiin:
```
[INFO] --- exec:1.2.1:exec (install-git-hooks) @ valintaperusteet-service ---
pre-commit installed at .git/hooks/pre-commit
```

Commitin yhteydessä spotless yrittää korjata tyylivirheet. Jos korjaaminen ei onnistu, commit epäonnistuu virheviestiin ja virheet tulee korjata käsin.

## Testien ajaminen

``mvn package``

Jos haluat ajaa tietokantaa käyttäviä testejä erikseen IDEstä, käynnistä PostgreSQL niitä varten:

``mvn -pl valintaperusteet-service/pom.xml -Dtestikannan.portti=4320 docker:start``

Jos porttia ei anneta, docker-maven-plugin arpoo vapaan portin.

Testi-PostgreSQL:n sammuttaminen hoituu samaan tapaan `mvn -pl valintaperusteet-service/pom.xml docker:stop`.

Huom: pelkkä `mvn test` jättää testikannan käyntiin. Se näkyy `docker ps`:llä.
