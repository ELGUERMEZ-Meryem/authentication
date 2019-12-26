package com.authentication.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public User(@NotBlank String email, @NotBlank String password) {
        this.email = email;
        this.password = password;
    }

    @Id
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Date createdAt;
}
