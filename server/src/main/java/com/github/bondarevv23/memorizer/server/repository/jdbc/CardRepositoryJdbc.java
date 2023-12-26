package com.github.bondarevv23.memorizer.server.repository.jdbc;

import com.github.bondarevv23.memorizer.server.model.Card;
import com.github.bondarevv23.memorizer.server.model.Side;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.SideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CardRepositoryJdbc extends AbstractJdbcRepository<Card> implements CardRepository {
    private final JdbcTemplate jdbc;
    private final SideRepository sideRepository;
    private final DeckRepository deckRepository;

    private static final String COUNT_BY_ID = """
            SELECT count(*)
            FROM card
            WHERE id = ?
            """;
    private static final String ADD_NEW = """
            INSERT INTO card (deck_id, question, answer)
            VALUES (?, ?, ?)
            RETURNING *
            """;
    private static final String UPDATE_BY_ID = """
            UPDATE card
            SET deck_id = ?
            WHERE id = ?
            """;
    private static final String SELECT_BY_ID = """
            SELECT *
            FROM card
            WHERE card.id = ?
            """;
    private static final String DELETE_BY_ID = """
            DELETE
            FROM card
            WHERE id = ?
            """;
    private static final String SELECT_ALL_BY_DECK_ID = """
            SELECT *
            FROM card
            WHERE deck_id = ?
            ORDER BY id
            """;
    private static final String SELECT_ALL = """
            SELECT *
            FROM card
            """;
    private static final String SELECT_RANDOM_BY_DECK_ID = """
            SELECT *
            FROM card
            WHERE deck_id = ?
            ORDER BY random()
            LIMIT 1
            """;

    @Transactional
    public Card save(Card card) {
        int affectedRows = Objects.requireNonNull(jdbc.queryForObject(COUNT_BY_ID, Integer.class, card.getId()));
        return addOrUpdate(affectedRows, this::addNew, this::update, card);
    }

    private Card addNew(Card card) {
        Side question = sideRepository.save(card.getQuestion());
        Side answer = sideRepository.save(card.getAnswer());
        Optional<Card> optional = jdbc.query(
                ADD_NEW,
                this::parseResultSet,
                card.getDeck().getId(),
                question.getId(),
                answer.getId());
        return orThrow(optional);
    }

    private Card update(Card card) {
        sideRepository.save(card.getQuestion());
        sideRepository.save(card.getAnswer());
        jdbc.update(UPDATE_BY_ID, card.getDeck().getId(), card.getId());
        return card;
    }

    public Optional<Card> findCardById(Integer id) {
        return jdbc.query(SELECT_BY_ID, this::parseResultSet, id);
    }

    public void deleteCardById(Integer id) {
        Optional<Card> card = Objects.requireNonNull(jdbc.query(SELECT_BY_ID, this::parseResultSet, id));
        if (card.isPresent()) {
            jdbc.update(DELETE_BY_ID, id);
            sideRepository.deleteSideById(card.get().getQuestion().getId());
            sideRepository.deleteSideById(card.get().getAnswer().getId());
        }
    }

    public List<Card> findCardsByDeckId(Integer deckId) {
        return jdbc.query(SELECT_ALL_BY_DECK_ID, this::resultSetToList, deckId);
    }

    public List<Card> findAll() {
        return jdbc.query(SELECT_ALL, this::resultSetToList);
    }

    public Optional<Card> getRandomFromDeck(Integer deckId) {
        return jdbc.query(SELECT_RANDOM_BY_DECK_ID, this::parseResultSet, deckId);
    }

    protected Optional<Card> parseResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return Optional.of(Card.builder()
                    .id(rs.getInt(ID))
                    .deck(orThrow(deckRepository.findDeckById(rs.getInt(DECK_ID))))
                    .question(orThrow(sideRepository.findSideById(rs.getInt(QUESTION))))
                    .answer(orThrow(sideRepository.findSideById(rs.getInt(ANSWER))))
                    .build());
        }
        return Optional.empty();
    }
}
