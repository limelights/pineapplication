package com.pine.pine.repositories;

import com.pine.pine.models.Company;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class CompanyRepository {
    private static final RowMapper<Company> ROW_MAPPER = (resultSet, rowNum) -> new Company(
            resultSet.getString("id"),
            resultSet.getString("name"));

    private final JdbcClient jdbcClient;

    public CompanyRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Company save(Company company) {
        jdbcClient.sql("""
                INSERT INTO companies (id, name)
                VALUES (:id, :name)
                ON CONFLICT(id) DO UPDATE SET name = excluded.name
                """)
                .param("id", company.getId(), Types.VARCHAR)
                .param("name", company.getName(), Types.VARCHAR)
                .update();
        return company;
    }

    public Optional<Company> findById(String id) {
        return jdbcClient.sql("SELECT id, name FROM companies WHERE id = :id")
                .param("id", id, Types.VARCHAR)
                .query(ROW_MAPPER)
                .optional();
    }

    public List<Company> findAll() {
        return jdbcClient.sql("SELECT id, name FROM companies ORDER BY id")
                .query(ROW_MAPPER)
                .list();
    }
}
