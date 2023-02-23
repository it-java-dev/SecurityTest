package org.ua.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.ua.service.UserService;
import org.ua.entity.CustomUser;
import org.ua.enums.UserRole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Controller
public class MyController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public MyController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model) {
        User user = getCurrentUser();

        String login = user.getUsername();
        CustomUser dbUser = userService.findByLogin(login);

        model.addAttribute("login", login);
        model.addAttribute("roles", user.getAuthorities());
        model.addAttribute("admin", isAdmin(user));
        model.addAttribute("email", dbUser.getEmail());
        model.addAttribute("phone", dbUser.getPhone());
        model.addAttribute("photo", dbUser.getPhoto());
        model.addAttribute("address", dbUser.getAddress());

        if (!dbUser.getPhoto().equals("")) {
            String path = System.getProperty("user.dir") + "/src/main/resources/photos/" + dbUser.getPhoto();
            byte[] img;
            try {
                img = Files.readAllBytes(Paths.get(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String base64 = Base64.getEncoder().encodeToString(img);
            model.addAttribute("base64", base64);
        }

        return "index";
    }

    @PostMapping(value = "/update")
    public String update(@RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String address,
                         @RequestParam(required = false) MultipartFile photo) {
        User user = getCurrentUser();

        String photoName = null;
        if (photo != null && !photo.getOriginalFilename().isEmpty()) {
            makeDir();
            try {
                photo.transferTo(new File
                        (System.getProperty("user.dir") +
                                "/src/main/resources/photos/" +
                                userService.findByLogin(getCurrentUser().getUsername()).getLogin()
                                + "_" + photo.getOriginalFilename()));
                photoName = userService.findByLogin(getCurrentUser().getUsername()).getLogin()
                        + "_" + photo.getOriginalFilename();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String login = user.getUsername();
        userService.updateUser(login, email, phone, address, photoName);

        return "redirect:/";
    }

    @PostMapping(value = "/newuser")
    public String update(@RequestParam String login,
                         @RequestParam String password,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String address,
                         @RequestParam(required = false) MultipartFile photo,
                         Model model) {
        String passHash = passwordEncoder.encode(password);

        if (password.length() < 8) {
            model.addAttribute("password", true);
            return "register";
        }
        String photoName = "";
        if (photo != null && !photo.getOriginalFilename().isEmpty()) {
            makeDir();
            try {
                photo.transferTo(new File
                        (System.getProperty("user.dir") +
                                "/src/main/resources/photos/" +
                                login + "_" + photo.getOriginalFilename()));
                photoName = login + "_" + photo.getOriginalFilename();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (!userService.addUser(login, passHash, UserRole.USER, email, phone, address, photoName)) {
            model.addAttribute("exists", true);
            model.addAttribute("login", login);
            return "register";
        }

        return "redirect:/";
    }

    private void makeDir(){
        File file = new File(System.getProperty("user.dir") +
                "/src/main/resources/photos/");
        if(!file.exists()) {
            file.mkdir();
        }
    }

    @PostMapping(value = "/delete")
    public String delete(@RequestParam(name = "toDelete[]", required = false) List<Long> ids,
                         Model model) {
        userService.deleteUsers(ids);
        model.addAttribute("users", userService.getAllUsers());

        return "admin";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // SpEL !!!
    public String admin(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/unauthorized")
    public String unauthorized(Model model) {
        User user = getCurrentUser();
        model.addAttribute("login", user.getUsername());
        return "unauthorized";
    }

    // ----

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private boolean isAdmin(User user) {
        Collection<GrantedAuthority> roles = user.getAuthorities();

        for (GrantedAuthority auth : roles) {
            if ("ROLE_ADMIN".equals(auth.getAuthority()))
                return true;
        }

        return false;
    }
}
