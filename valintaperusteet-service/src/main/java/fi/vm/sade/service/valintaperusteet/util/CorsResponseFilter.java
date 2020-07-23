package fi.vm.sade.service.valintaperusteet.util;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

public class CorsResponseFilter implements ContainerResponseFilter {
  @Override
  public void filter(
      ContainerRequestContext containerRequestContext, ContainerResponseContext responseContext)
      throws IOException {
    MultivaluedMap<String, Object> headers = responseContext.getHeaders();
    headers.add("Access-Control-Allow-Origin", "*");
  }
}
