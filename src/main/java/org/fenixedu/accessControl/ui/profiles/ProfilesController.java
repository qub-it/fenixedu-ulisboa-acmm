package org.fenixedu.accessControl.ui.profiles;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;

@SpringApplication(group = "academic(MANAGE_AUTHORIZATIONS)", path = "profiles", title = "title.Accesscontrol.Profiles",
        hint = "Access Control")
@SpringFunctionality(app = ProfilesController.class, title = "title.Accesscontrol.Profiles")
public class ProfilesController {

}
