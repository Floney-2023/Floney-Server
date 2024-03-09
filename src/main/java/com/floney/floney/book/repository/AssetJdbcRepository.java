package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Asset;
import com.floney.floney.common.constant.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssetJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<Asset> assets) {
        String sql = "INSERT INTO asset (date, money, book_id,created_at,updated_at,status)" +
            "VALUES (?,?,?,now(),now(),?) ON DUPLICATE KEY UPDATE money = money + VALUES(money), updated_at = NOW()";

        jdbcTemplate.batchUpdate(sql,
            assets,
            assets.size(),
            (PreparedStatement ps, Asset asset) -> {
                ps.setString(1, asset.getDate().toString());
                ps.setDouble(2, asset.getMoney());
                ps.setLong(3, asset.getBook().getId());
                ps.setString(4, Status.ACTIVE.name());
            });
    }
}
