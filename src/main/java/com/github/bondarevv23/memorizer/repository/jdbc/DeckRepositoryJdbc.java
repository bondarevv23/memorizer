package com.github.bondarevv23.memorizer.repository.jdbc;

import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
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
public class DeckRepositoryJdbc extends AbstractJdbcRepository<Deck> implements DeckRepository {
    private final JdbcTemplate jdbc;

    private static final String COUNT_BY_ID = """
            SELECT count(*)
            FROM deck
            WHERE id = ?
            """;
    private static final String ADD_NEW = """
            INSERT INTO deck (owner, title)
            VALUES (?, ?)
            RETURNING *
            """;
    private static final String UPDATE_BY_ID = """
            UPDATE deck
            SET title = ?,
                owner = ?
            WHERE id = ?
            """;
    private static final String DELETE_BY_ID = """
            DELETE
            FROM deck
            WHERE id = ?
            """;
    private static final String selectById = """
            SELECT *
            FROM deck
            WHERE id = ?
            """;
    private static final String selectAll = """
            SELECT *
            FROM deck
            """;

    public Deck save(Deck deck) {
        int affectedRows = Objects.requireNonNull(jdbc.queryForObject(COUNT_BY_ID, Integer.class, deck.getId()));
        return addOrUpdate(affectedRows, this::addNew, this::update, deck);
    }

    private Deck addNew(Deck deck) {
        Optional<Deck> optional = jdbc.query(ADD_NEW, this::parseResultSet, deck.getOwner(), deck.getTitle());
        return orThrow(optional);
    }

    private Deck update(Deck deck) {
        jdbc.update(UPDATE_BY_ID, deck.getTitle(), deck.getOwner(), deck.getId());
        return deck;
    }

    public void deleteDeckById(Integer id) {
        jdbc.update(DELETE_BY_ID, id);
    }

    public Optional<Deck> findDeckById(Integer id) {
        return jdbc.query(selectById, this::parseResultSet, id);
    }

    public List<Deck> findAll() {
        return jdbc.query(selectAll, this::resultSetToList);
    }

    protected Optional<Deck> parseResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return Optional.of(Deck.builder()
                    .id(rs.getInt(ID))
                    .title(rs.getString(TITLE))
                    .owner(rs.getLong(OWNER))
                    .build());
        }
        return Optional.empty();
    }
}
