package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.User;
import com.pitospies.pitospieswebservice.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api", produces="application/json")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;


    @GetMapping("/register/{username}/{password}")
    public ResponseEntity<User> handleRequest(@Valid @PathVariable String username,
                                              @Valid @PathVariable String password) {

        User user = new User(null, username, password, null);

        authService.registerUser(
                new User(null, username, password, null)
        );

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}