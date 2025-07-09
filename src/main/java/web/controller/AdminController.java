package web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public ModelAndView index(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("admin");
        modelAndView.addObject("allRoles", roleService.findAll());
        User currentUser = userService.findByEmail(principal.getName()).orElseThrow();
        modelAndView.addObject("user", currentUser);
        return modelAndView;
    }
}
