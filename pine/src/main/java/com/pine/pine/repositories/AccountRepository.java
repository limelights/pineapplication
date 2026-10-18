package com.pine.pine.repositories;

import com.pine.pine.models.Account;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {
    private static final RowMapper<Account> ROW_MAPPER = (resultSet, rowNum) -> new Account(
            resultSet.getLong("uid"),
            resultSet.getString("name"),
            resultSet.getString("id"));

    private final JdbcClient jdbcClient;

    public AccountRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Account save(Account account) {
        Long uid = jdbcClient.sql("""
                INSERT INTO accounts (uid, id, name)
                VALUES (:uid, :id, :name)
                ON CONFLICT(uid) DO UPDATE SET id = excluded.id, name = excluded.name
                RETURNING uid
                """)
                .param("uid", account.getUid(), Types.BIGINT)
                .param("id", account.getId(), Types.VARCHAR)
                .param("name", account.getName(), Types.VARCHAR)
                .query(Long.class)
                .single();
        account.setUid(uid);
        return account;
    }

    public Optional<Account> findByUid(long uid) {
        return jdbcClient.sql("SELECT uid, id, name FROM accounts WHERE uid = :uid")
                .param("uid", uid)
                .query(ROW_MAPPER)
                .optional();
    }

    public List<Account> findAll() {
        return jdbcClient.sql("SELECT uid, id, name FROM accounts ORDER BY uid")
                .query(ROW_MAPPER)
                .list();
    }
}
