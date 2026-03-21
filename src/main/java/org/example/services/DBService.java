package org.example.services;

import org.springframework.jdbc.core.JdbcTemplate;

public class ClickHouseService {
    private final JdbcTemplate jdbcTemplate;

    public ClickHouseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


}
