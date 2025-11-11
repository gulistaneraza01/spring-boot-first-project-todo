package com.example.todo_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public List<TodoDto> searchTodos(String query) {
        List<Todo> todos = todoRepository.findByTitleContainingIgnoreCase(query);
        return todos.stream()
                .map(this::convertToDto)
                .toList();
    }

    public String toggleTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id not found: " + id));
        boolean completed = "true".equalsIgnoreCase(todo.getCompleted());
        todo.setCompleted(String.valueOf(!completed));
        todoRepository.save(todo);
        return "successfully toggle the todo";
    }

    public int uploadExcelFile(MultipartFile file) throws Exception {
        List<Todo> todos = new ArrayList<>();

        InputStream inputStream = file.getInputStream();

        Workbook workbook = null;
        String fileName = file.getOriginalFilename();

        if (fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        }

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Todo todo = new Todo();

            Cell titleCell = row.getCell(0);
            Cell completedCell = row.getCell(1);

            if (titleCell != null) {
                todo.setTitle(getCellValueAsString(titleCell));
            }

            if (completedCell != null) {
                String completedValue = getCellValueAsString(completedCell);
                todo.setCompleted(completedValue);
            } else {
                todo.setCompleted("false");
            }

            todos.add(todo);
        }

        workbook.close();

        todoRepository.saveAll(todos);

        return todos.size();
    }

    public byte[] downloadExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Todos");
            List<Todo> todos = todoRepository.findAll();

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Title");
            header.createCell(2).setCellValue("Completed");

            // Fill the data
            int rowNum = 1;
            for (Todo todo : todos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(todo.getId());
                row.createCell(1).setCellValue(todo.getTitle());
                row.createCell(2).setCellValue(todo.getCompleted());
            }

            // Auto-size columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
