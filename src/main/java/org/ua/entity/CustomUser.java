package org.ua.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ua.enums.UserRole;

import javax.persistence.*;

@Entity
@Data @NoArgsConstructor
public class CustomUser {
    @Id
    @GeneratedValue
    private Long id;

    private String login;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String email;
    private String phone;

    private String photo;
    private String address;

//    public CustomUser(String login, String password, UserRole role,
//                      String email, String phone, String address) {
//        this.login = login;
//        this.password = password;
//        this.role = role;
//        this.email = email;
//        this.phone = phone;
//        this.address = address;
//    }

    public CustomUser(String login, String password, UserRole role, String email, String phone, String photo, String address) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.address = address;
    }
}
