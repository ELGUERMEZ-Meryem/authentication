package com.authentication.api.entity;

import com.authentication.api.enums.TwofaTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity<String> {
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private Boolean is_2fa_enabled;
    private String code_2fa;
    private Date expire_time_2fa;
    private TwofaTypes default_type_2fa;

}
