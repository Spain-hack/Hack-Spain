package org.example.repositories;

import org.example.entities.TableRequest;
import org.example.entities.TableResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BankRepository {
    private static JdbcTemplate jdbcTemplate;
    public BankRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAll() {
        String sql = """
        SELECT
                t.table_name,
                obj_description((quote_ident(t.table_schema) || '.' || quote_ident(t.table_name))::regclass) AS table_comment,
                c.column_name,
                c.data_type,
                c.udt_name,
                col_description(
                    (quote_ident(c.table_schema) || '.' || quote_ident(c.table_name))::regclass::oid,
                    c.ordinal_position
                ) AS column_comment
            FROM information_schema.tables t
            JOIN information_schema.columns c
              ON t.table_name = c.table_name
              AND t.table_schema = c.table_schema
            WHERE t.table_schema = 'public'
              AND t.table_type = 'BASE TABLE'
            ORDER BY t.table_name, c.ordinal_position
        """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        Map<String, Map<String, Object>> tablesMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String tableName = (String) row.get("table_name");

            if (!tablesMap.containsKey(tableName)) {
                Map<String, Object> table = new LinkedHashMap<>();
                table.put("id", tableName);
                table.put("name", tableName);
                table.put("description", row.getOrDefault("table_comment", ""));
                table.put("columns", new ArrayList<Map<String, Object>>());
                tablesMap.put(tableName, table);
            }
                //@SuppressWarnings("unchecked")
                List<Map<String, Object>> columns = (List<Map<String, Object>>) tablesMap.get(tableName).get("columns");
                Map<String, Object> column = new LinkedHashMap<>();
                column.put("name", row.get("column_name"));
                column.put("type", mapPostgresTypeToJsonType((String) row.get("data_type")));
                column.put("description", row.getOrDefault("column_comment", ""));
                columns.add(column);
        }
        return new ArrayList<>(tablesMap.values());
    }

        private static String mapPostgresTypeToJsonType(String pgType) {
            if (pgType == null) return "String";
            pgType = pgType.toLowerCase();
            switch (pgType) {
                case "integer", "serial", "bigint", "smallint": return "Number";
                case "decimal", "numeric", "real", "double precision": return "Decimal";
                case "date": return "Date";
                case "timestamp", "timestamp without time zone", "timestamp with time zone": return "DateTime";
                default: return "String";
            }
        }

    public Map<String, List<Map<String, Object>>> mergeTable(TableRequest request) {

        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();

        List<String> tables = request.getTables();
        int limit = request.getLimit() != null ? request.getLimit() : 10;

        if (tables == null || tables.isEmpty()) {
            return result;
        }

        for (String table : tables) {

            String sql = "SELECT * FROM " + table + " LIMIT " + limit;

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            result.put(table, rows);
        }

        return result;
    }


}
