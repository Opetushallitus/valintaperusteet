package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.App;
import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping(value = "/")
public class StaticResource {

  @GetMapping(value = {"/swagger", "/swagger/**"})
  public void swagger(HttpServletResponse response) throws IOException {
    response.sendRedirect(App.CONTEXT_PATH.concat("/swagger-ui/index.html"));
  }
}
