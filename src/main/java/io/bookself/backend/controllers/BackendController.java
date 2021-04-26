package io.bookself.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    /**
     * DO NOT REMOVE - This is used as the healthcheck endpoint for the load balancer.
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
