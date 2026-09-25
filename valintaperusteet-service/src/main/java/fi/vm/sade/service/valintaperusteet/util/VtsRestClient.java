package fi.vm.sade.service.valintaperusteet.util;

import static fi.vm.sade.valinta.sharedutils.http.HttpResource.CSRF_VALUE;
import static org.asynchttpclient.Dsl.asyncHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import fi.vm.sade.javautils.nio.cas.CasClient;
import fi.vm.sade.service.valintaperusteet.config.ConfigEnums;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VtsRestClient {
  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(
              Date.class,
              (JsonDeserializer<Date>)
                  (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
          .create();
  private final AsyncHttpClient asyncHttpClient = asyncHttpClient();
  private final String serviceUrl;
  private final CasClient casClient;

  @Autowired
  public VtsRestClient(
      @Value("${valintaperusteet.valinta-tulos-service.service-url}") final String serviceUrl,
      @Qualifier("ValintatulosCasClient") final CasClient casClient) {
    this.serviceUrl = serviceUrl;
    this.casClient = casClient;
  }

  public boolean isJonoSijoiteltu(final String jonoOid) {
    try {
      final String jonoUrl = String.format("%s/sijoittelu/jono/%s", serviceUrl, jonoOid);
      final Response response =
          casClient.executeAndRetryWithCleanSessionOnStatusCodesBlocking(
              new RequestBuilder()
                  .setUrl(jonoUrl)
                  .setMethod("GET")
                  .addHeader("Accept", "application/json")
                  .setRequestTimeout(Duration.of(120000, ChronoUnit.MILLIS))
                  .setReadTimeout(Duration.of(120000, ChronoUnit.MILLIS))
                  .addHeader("Caller-Id", ConfigEnums.CALLER_ID.value())
                  .addHeader("CSRF", CSRF_VALUE)
                  .addHeader("Cookie", String.format("CSRF=%s;", CSRF_VALUE))
                  .build(),
              Set.of(302, 401));

      if (response.getStatusCode() == 200) {
        final TypeToken<Map<String, Boolean>> typeToken = new TypeToken<>() {};
        final Map<String, Boolean> result =
            GSON.fromJson(response.getResponseBody(), typeToken.getType());
        final Boolean exists = result.get("IsSijoiteltu");
        if (exists == null) return false;
        return exists;
      } else {
        throw new RuntimeException(
            String.format(
                "Valinta-tulos-service returned non-ok status %s: %s",
                response.getStatusCode(), response.getResponseBody()));
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
