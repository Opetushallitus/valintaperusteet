package fi.vm.sade.service.valintaperusteet.util;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import java.util.Map;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public VtsRestClient(
      @Value("${valintaperusteet.valinta-tulos-service.service-url}") final String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public boolean isJonoSijoiteltu(final String jonoOid) {
    try {
      final String jonoUrl = String.format("%s/sijoittelu/jono/%s", serviceUrl, jonoOid);
      final Response response =
          asyncHttpClient
              .executeRequest(
                  new RequestBuilder()
                      .setUrl(jonoUrl)
                      .setMethod("GET")
                      .addHeader("Accept", "application/json")
                      .setRequestTimeout(120000)
                      .setReadTimeout(120000)
                      .build())
              .toCompletableFuture()
              .get();

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
