package com.shubham.taskflow.controller;

import com.shubham.taskflow.dto.TaskRequest;
import com.shubham.taskflow.dto.TaskResponse;
import com.shubham.taskflow.model.TaskStatus;
import com.shubham.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/tasks?projectId=1&status=IN_PROGRESS&page=0&size=10&sort=dueDate,asc
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findAll(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.findAll(projectId, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
