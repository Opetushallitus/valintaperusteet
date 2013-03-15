package fi.vm.sade.valintaperusteet.portlet;

import fi.vm.sade.valintaperusteet.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * User: tommiha
 * Date: 2/15/13
 * Time: 2:43 PM
 */
@RequestMapping("VIEW")
@Controller
public class ValintaperusteetController {

    @Autowired
    Configuration configuration;

    @RenderMapping
    public String index(Model model) {
        model.addAttribute("serviceUrl", configuration.getServiceBaseUrl());
        return "index";
    }
}
