package com.authentication.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity<String> {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
