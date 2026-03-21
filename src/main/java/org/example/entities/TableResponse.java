package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private String id;
    private String name;
    private String description;
    private List<Map<String, Object>> columns;
}
