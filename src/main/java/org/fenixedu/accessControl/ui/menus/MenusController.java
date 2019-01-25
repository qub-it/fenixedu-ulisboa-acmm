package org.fenixedu.accessControl.ui.menus;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/items")
@SpringApplication(group = "academic(MANAGE_AUTHORIZATIONS)", path = "menus", title = "title.Accesscontrol.Menus",
        hint = "Access Control")
@SpringFunctionality(app = MenusController.class, title = "title.Accesscontrol.Menus")
public class MenusController {

    @RequestMapping(method = RequestMethod.GET)
    private String init(Model model) {

        final Set<MenuItem> menus = getMenu(PortalConfiguration.getInstance().getMenu().getOrderedChild());

        model.addAttribute("menus", menus);
        return "menus/menus";
    }

    private Set<MenuItem> getMenu(Set<MenuItem> menus) {

        final Set<MenuItem> items = new HashSet<>();

        for (final MenuItem menuItem : menus) {
            if (menuItem.isMenuContainer()) {
                final Set<MenuItem> submenus = ((MenuContainer) menuItem).getOrderedChild();
                items.addAll(getMenu(submenus));
            } else {
                items.add(menuItem);
            }
        }

        return items;

    }

}
