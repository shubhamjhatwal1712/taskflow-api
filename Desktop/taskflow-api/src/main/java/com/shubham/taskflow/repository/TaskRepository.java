package com.shubham.taskflow.repository;

import com.shubham.taskflow.model.Task;
import com.shubham.taskflow.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
}
