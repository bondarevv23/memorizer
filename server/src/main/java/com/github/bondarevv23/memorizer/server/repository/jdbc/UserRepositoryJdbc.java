package com.github.bondarevv23.memorizer.server.repository.jdbc;

import com.github.bondarevv23.memorizer.server.model.User;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UserRepositoryJdbc extends AbstractJdbcRepository<User> implements UserRepository {
    // NamedParameterJdbcTemplate
    private final JdbcTemplate jdbc;
    private final DeckRepository deckRepository;

    private static final String ADD_NEW = """
            INSERT INTO _user (tg_id)
            VALUES (?)
            RETURNING *
            """;
    private static final String FIND_BY_TG_ID = """
            SELECT *
            FROM _user
            WHERE tg_id = ?
            """;
    private static final String FIND_ALL_IDS = """
            SELECT tg_id
            FROM _user
            """;
    private static final String DELETE_BY_ID = """
            DELETE
            FROM _user
            WHERE tg_id = ?
            """;
    private static final String SELECT_DECK_IDS = """
            SELECT deck_id
            FROM user_deck
            WHERE user_tg_id = ?
            """;
    private static final String DELETE_DECK_FROM_USER = """
            DELETE
            FROM user_deck
            WHERE user_tg_id = ? AND deck_id = ?
            """;
    private static final String ADD_USER_DECK = """
            INSERT INTO user_deck (user_tg_id, deck_id)
            VALUES (?, ?)
            """;

    public User save(User user) {
        Optional<User> optUser = jdbc.query(ADD_NEW, this::parseResultSet, user.getTgId());
        return orThrow(optUser);
    }

    public Optional<User> findUserByTgId(Long id) {
//        jdbc.query(FIND_BY_TG_ID, new MapSqlParameterSource().addValue("tg_id", id), new User());
        return jdbc.query(FIND_BY_TG_ID, this::parseResultSet, id);
    }

    public List<User> findAll() {
        return jdbc.queryForList(FIND_ALL_IDS, Long.class).stream()
                .map(this::findUserByTgId)
                .map(this::orThrow)
                .toList();
    }

    public void deleteUserByTgId(Long id) {
        jdbc.update(DELETE_BY_ID, id);
    }

    public void removeDeckFromUser(Long id, Integer deckId) {
        jdbc.update(DELETE_DECK_FROM_USER, id, deckId);
    }

    public void shareDeckWithUser(Long id, Integer deckId) {
        jdbc.update(ADD_USER_DECK, id, deckId);
    }

    protected Optional<User> parseResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            long tgId = rs.getLong(TG_ID);
            return Optional.of(User.builder()
                    .tgId(tgId)
                    .decks(jdbc.queryForList(SELECT_DECK_IDS, Integer.class, tgId).stream()
                            .map(deckRepository::findDeckById)
                            .map(this::orThrow)
                            .collect(Collectors.toList()))
                    .build());
        }
        return Optional.empty();
    }
}
