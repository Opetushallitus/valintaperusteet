package fi.vm.sade.service.valintaperusteet.resource;

import static fi.vm.sade.service.valintaperusteet.roles.ValintaperusteetRole.OPH_CRUD;

import fi.vm.sade.service.valintaperusteet.service.LuoValintaperusteetService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/resources/luovalintaperusteet")
@PreAuthorize("isAuthenticated()")
public class LuoValintaperusteetResource {
  @Autowired private LuoValintaperusteetService luoValintaperusteetService;

  @GetMapping(value = "/luo")
  @PreAuthorize(OPH_CRUD)
  public ResponseEntity<Object> luo() {
    try {
      luoValintaperusteetService.luo();
      return ResponseEntity.accepted().build();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
