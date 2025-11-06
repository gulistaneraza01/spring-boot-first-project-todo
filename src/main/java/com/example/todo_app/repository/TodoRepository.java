package com.example.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todo_app.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
