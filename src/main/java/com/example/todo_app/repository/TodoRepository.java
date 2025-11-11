package com.example.todo_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todo_app.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTitleContainingIgnoreCase(String title);

}
