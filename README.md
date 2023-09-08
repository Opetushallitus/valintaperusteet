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

## Testien ajaminen

``mvn package``

Jos haluat ajaa tietokantaa käyttäviä testejä erikseen IDEstä, käynnistä PostgreSQL niitä varten:

``mvn -pl valintaperusteet-service/pom.xml -Dtestikannan.portti=4320 docker:start``

Jos porttia ei anneta, docker-maven-plugin arpoo vapaan portin.

Testi-PostgreSQL:n sammuttaminen hoituu samaan tapaan `mvn -pl valintaperusteet-service/pom.xml docker:stop`.

Huom: pelkkä `mvn test` jättää testikannan käyntiin. Se näkyy `docker ps`:llä.
