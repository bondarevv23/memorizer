package com.github.bondarevv23.memorizer.server.repository.jdbc;

import com.github.bondarevv23.memorizer.server.model.Side;
import com.github.bondarevv23.memorizer.server.repository.interfaces.SideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SideRepositoryJdbc extends AbstractJdbcRepository<Side> implements SideRepository {
    private final JdbcTemplate jdbc;

    private static final String COUNT_BY_ID = """
            SELECT count(*)
            FROM side
            WHERE id = ?
            """;
    private static final String ADD_NEW = """
            INSERT INTO side (title, text, image_link)
            VALUES (?, ?, ?)
            RETURNING *
            """;
    private static final String UPDATE_BY_ID = """
            UPDATE side
            SET title = ?,
                text = ?,
                image_link = ?
            WHERE id = ?
            """;
    private static final String DELETE_BY_ID = """
            DELETE FROM side
            WHERE id = ?
            """;
    private static final String SELECT_BY_ID = """
            SELECT *
            FROM side
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT *
            FROM side
            """;

    public Side save(Side side) {
        int affectedRows = Objects.requireNonNull(jdbc.queryForObject(COUNT_BY_ID, Integer.class, side.getId()));
        return addOrUpdate(affectedRows, this::addNew, this::update, side);
    }

    private Side addNew(Side side) {
        Optional<Side> optSide = jdbc.query(
                ADD_NEW,
                this::parseResultSet,
                side.getTitle(),
                side.getText(),
                side.getImageLink());
        return orThrow(optSide);
    }

    private Side update(Side side) {
        jdbc.update(UPDATE_BY_ID, side.getTitle(), side.getText(), side.getImageLink(), side.getId());
        return side;
    }

    public void deleteSideById(Integer id) {
        jdbc.update(DELETE_BY_ID, id);
    }

    public Optional<Side> findSideById(Integer id) {
        return jdbc.query(SELECT_BY_ID, this::parseResultSet, id);
    }

    public List<Side> findAll() {
        return jdbc.query(SELECT_ALL, this::resultSetToList);
    }

    protected Optional<Side> parseResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return Optional.of(Side.builder()
                    .id(rs.getInt(ID))
                    .title(rs.getString(TITLE))
                    .text(rs.getString(TEXT))
                    .imageLink(rs.getString(IMAGE_LINK))
                    .build());
        }
        return Optional.empty();
    }
}
