package com.example.todo_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.todo_app.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import com.example.todo_app.model.Todo;
import com.example.todo_app.dto.TodoCraeteDto;
import com.example.todo_app.dto.TodoDto;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public Page<TodoDto> getAllTodos(int pageno, int size) {
        Pageable pageable = PageRequest.of(pageno, size);
        Page<Todo> todoPage = todoRepository.findAll(pageable);
        return todoPage.map(this::convertToDto);
    }

    public TodoDto getTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id not found: " + id));
        return convertToDto(todo);
    }

    public TodoDto convertToDto(Todo todo) {
        boolean completed = "true".equalsIgnoreCase(todo.getCompleted());
        return new TodoDto(todo.getId(), todo.getTitle(), completed);
    }

    public String createTodo(TodoCraeteDto todoCraeteDto) {
        Todo todo = new Todo();
        todo.setTitle(todoCraeteDto.getTitle());
        todo.setCompleted(String.valueOf(todoCraeteDto.isCompleted()));
        todoRepository.save(todo);
        return "Todo created successfully.";
    }

    public TodoDto deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id not found: " + id));
        todoRepository.deleteById(id);
        return convertToDto(todo);
    }

    public String toggleTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id not found: " + id));
        boolean completed = "true".equalsIgnoreCase(todo.getCompleted());
        todo.setCompleted(String.valueOf(!completed));
        todoRepository.save(todo);
        return "successfully toggle the todo";
    }
}
