package org.example.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public class BankRepository {
    private static JdbcTemplate jdbcTemplate;
    public BankRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Double getTotalAmount() {
        String sql = "SELECT SUM(amount) FROM transactions";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public List<Map<String, Object>> getByCity() {
        String sql = """
            SELECT 
                c.city,
                SUM(t.amount) AS total_amount
            FROM transactions t
            JOIN accounts a ON t.account_id = a.id
            JOIN client c ON a.client_id = c.id
            GROUP BY c.city
        """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getByWallet() {
        String sql = """
            SELECT 
                a.type_of_wallet,
                SUM(t.amount) AS total_amount
            FROM transactions t
            JOIN accounts a ON t.account_id = a.id
            GROUP BY a.type_of_wallet
        """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getAll() {
        String sql = """
                    SELECT table_name, column_name, data_type
                    FROM information_schema.columns
                    WHERE table_schema = 'public'
                    ORDER BY table_name, ordinal_position
                """;

        return jdbcTemplate.queryForList(sql);
    }
}
