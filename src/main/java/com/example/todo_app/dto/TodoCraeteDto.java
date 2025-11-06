package com.example.todo_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TodoCraeteDto {
    private String title;
    private boolean completed;
}
