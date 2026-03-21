package org.example.controllers;

import org.example.repositories.BankRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final BankRepository repository;

    public TaskController(BankRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public List<Map<String, Object>> getTask(){
        return repository.getAll();
    }
}
