package com.example.todo_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public int[] hello() {
        int[] nums = { 1, 23, 4 };
        return nums;
    }
}
