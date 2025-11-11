package com.example.todo_app.controller;

import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import com.example.todo_app.dto.TodoCraeteDto;
import com.example.todo_app.dto.TodoDto;
import com.example.todo_app.service.TodoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TodoController {
    private final TodoService todoService;

    @GetMapping("/allTodo")
    public ResponseEntity<Page<TodoDto>> getTodos(
            @RequestParam(defaultValue = "0") int pageno,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(todoService.getAllTodos(pageno, size));
    }

    @GetMapping("/allTodo/{id}")
    public ResponseEntity<TodoDto> getTode(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodo(id));
    }

    @PostMapping("/create-todo")
    public ResponseEntity<String> createTodo(@RequestBody TodoCraeteDto todoCraeteDto) {
        return ResponseEntity.ok(todoService.createTodo(todoCraeteDto));
    }

    @PostMapping("/uploade-excel")
    public ResponseEntity<String> uploadeExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("plzz select a file to upload");
            }

            todoService.uploadExcelFile(file);

            return ResponseEntity.ok("todos uploaded successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/download-todo")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] excelBytes = todoService.downloadExcelFile();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=todos.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/toggle-todo/{id}")
    public ResponseEntity<String> toggleTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.toggleTodo(id));
    }

    @DeleteMapping("/delete-todo/{id}")
    public ResponseEntity<TodoDto> deleteTode(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.deleteTodo(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TodoDto>> searchTodos(@RequestParam String query) {
        return ResponseEntity.ok(todoService.searchTodos(query));
    }
}
