package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    Integer id;
    String username;
    String password;
    String fullname;
    String email;
    String tel;
    String status;
    String code;

    @Transient
    private List<String> roles;
}