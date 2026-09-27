package com.pitospies.pitospieswebservice.webservices;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api", produces="application/json")
@Validated
public class ValidationJSONRestController {
}
