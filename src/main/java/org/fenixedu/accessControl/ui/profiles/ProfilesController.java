package org.fenixedu.accessControl.ui.profiles;

import java.util.Set;

import org.fenixedu.accessControl.domain.ProfileSC;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/profiles")
@SpringApplication(group = "academic(MANAGE_AUTHORIZATIONS)", path = "profiles", title = "title.Accesscontrol.Profiles",
        hint = "Access Control")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles")
public class ProfilesController {

    @RequestMapping(method = RequestMethod.GET)
    private String init(Model model) {

        final Set<ProfileSC> profiles = Bennu.getInstance().getProfilescSet();

        model.addAttribute("profiles", profiles);
        return "profiles/profiles";
    }

}
