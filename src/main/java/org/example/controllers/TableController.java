package org.example.controllers;

import org.example.entities.NewTable;
import org.example.entities.TableRequest;
import org.example.repositories.BankRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/table")
public class TableController {
    private final BankRepository repository;

    public TableController(BankRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public List<Map<String, Object>> getTask(){
        return repository.getAll();
    }

    @PostMapping()
    public Map<String, List<Map<String, Object>>> handleTableRequest(@RequestBody NewTable request) {

        TableRequest table = new TableRequest(request.getTables(), request.getLimit());

        return repository.mergeTable(table);
    }

}
