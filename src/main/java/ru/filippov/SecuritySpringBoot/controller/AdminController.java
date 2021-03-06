package ru.filippov.SecuritySpringBoot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.filippov.SecuritySpringBoot.model.Role;
import ru.filippov.SecuritySpringBoot.model.User;
import ru.filippov.SecuritySpringBoot.service.RoleService;
import ru.filippov.SecuritySpringBoot.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping()
    public String getAllUser(Model model, Principal principal){
        model.addAttribute("userList", userService.listUsers());
        model.addAttribute("user", new User());
        model.addAttribute("currentUser", userService.getUserByEmail(principal.getName()));
        return "admin/index";
    }

    @DeleteMapping("/delete/{id}")
    public String removeUser(@PathVariable("id")long id, Model model){
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUsers(@PathVariable("id")long id, Model model){
        model.addAttribute("user", userService.getUserById(id));
        return "admin/edit";
    }

    @PutMapping("/{id}")
    public String editUser(@ModelAttribute("user") User user,
                           @PathVariable ("id")long id, @RequestParam(name = "role", required = false) String[] roles){
        if(roles!= null){
            Set<Role> rolesSet = new HashSet<>();
            for(String s: roles){
                rolesSet.add(roleService.findByName(s));
            }
            user.setRoles(rolesSet);
        } else{
            user.setRoles(userService.getUserById(user.getId()).getRoles());
        }
        if(user.getPassword().equals("")){
            user.setPassword(userService.getUserById(user.getId()).getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.update(id, user);
        return "redirect:/admin";
    }


    @PostMapping()
    public String addUser(@ModelAttribute("user")User user,
            @RequestParam(name = "role", required = false) String[] roles){
        Set<Role> rolesSet = new HashSet<>();
        for(String s: roles){
            rolesSet.add(roleService.findByName(s));
        }
        user.setRoles(rolesSet);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.add(user);
        return "redirect:/admin";
    }


}
