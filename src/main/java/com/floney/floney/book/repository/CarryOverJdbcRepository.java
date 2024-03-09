package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.CarryOver;
import com.floney.floney.common.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CarryOverJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<CarryOver> bookLines) {
        String sql = "INSERT INTO carry_over (date, money, book_id,created_at,updated_at,status)" +
            "VALUES (?,?,?,now(),now(),?) ON DUPLICATE KEY UPDATE money = money + VALUES(money), updated_at = NOW()";

        jdbcTemplate.batchUpdate(sql,
            bookLines,
            bookLines.size(),
            (PreparedStatement ps, CarryOver carryOver) -> {
                ps.setString(1, carryOver.getDate().toString());
                ps.setDouble(2, carryOver.getMoney());
                ps.setLong(3, carryOver.getBook().getId());
                ps.setString(4, Status.ACTIVE.name());
            });
    }

}
