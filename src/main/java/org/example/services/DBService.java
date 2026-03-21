package org.example.services;

import org.springframework.jdbc.core.JdbcTemplate;

public class DBService {
    private final JdbcTemplate jdbcTemplate;

    public DBService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void createOlapCube() {
        jdbcTemplate.execute("""
            CREATE MATERIALIZED VIEW IF NOT EXISTS transaction_cube AS
            SELECT 
                c.city,
                a.type_of_wallet,
                t.category,
                t.type,
                SUM(t.amount) AS total_amount,
                COUNT(*) AS transaction_count
            FROM transactions t
            JOIN accounts a ON t.account_id = a.id
            JOIN client c ON a.client_id = c.id
            GROUP BY CUBE(
                c.city,
                a.type_of_wallet,
                t.category,
                t.type
            )
        """);
    }

    public void refreshCube() {
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW transaction_cube");
    }


}
