package com.github.bondarevv23.memorizer.repository;

import com.github.bondarevv23.memorizer.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.util.TestUtil.getDeck;
import static com.github.bondarevv23.memorizer.util.TestUtil.updateDeck;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeckRepositoryJdbcTest extends IntegrationEnvironment {

    @Autowired
    private DeckRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    private Long owner;
    private Long wrongOwner;

    @BeforeAll
    public void createOwners() {
        owner = userRepository.save(User.builder().tgId(1L).build()).getTgId();
        wrongOwner = -1L;
    }

    @AfterAll
    public void deleteOwners() {
        userRepository.deleteUserByTgId(owner);
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewDeck_thenSuccessfullyAdded() {
        // given
        Deck deck = getDeck(owner, 1);

        // when
        Deck savedDeck = repository.save(deck);

        // then
        Optional<Deck> storedDeck = deckRepository.findDeckById(savedDeck.getId());
        assertThat(storedDeck).isNotNull();
        assertThat(storedDeck.isPresent()).isTrue();
        assertThat(storedDeck.get()).isEqualTo(savedDeck);
    }

    @Test
    @Transactional
    @Rollback
    void whenUpdateDeck_thenSuccessfullyUpdated() {
        // given
        Deck deck = repository.save(getDeck(owner, 1));
        updateDeck(deck);

        // when
        Deck updatedDeck = repository.save(deck);

        // then
        Optional<Deck> storedDeck = deckRepository.findDeckById(deck.getId());
        assertThat(storedDeck).isNotNull();
        assertThat(storedDeck.isPresent()).isTrue();
        assertThat(storedDeck.get()).isEqualTo(updatedDeck);
    }

    @Test
    @Transactional
    @Rollback
    void whenSaveNewDeckWithWrongOwner_thenDataAccessExceptionThrows() {
        // given
        Deck deck = getDeck(wrongOwner, 1);

        // when

        // then
        assertThatThrownBy(() -> repository.save(deck)).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    void whenFindExistingDeckById_thenFindSuccessfully() {
        // given
        Deck deck = repository.save(getDeck(owner, 1));

        // when
        Optional<Deck> storedDeck = repository.findDeckById(deck.getId());

        // then
        assertThat(storedDeck).isNotNull();
        assertThat(storedDeck.isPresent()).isTrue();
        assertThat(storedDeck.get()).isEqualTo(deck);
    }

    @Test
    @Transactional
    @Rollback
    void whenFindDeckByWringId_thenEmptyOptionalReturns() {
        // given
        repository.save(getDeck(owner, 1));

        // when
        Optional<Deck> deck = repository.findDeckById(-1);

        // then
        assertThat(deck).isNotNull();
        assertThat(deck.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteDeckByRightId_thenDeckHasSuccessfullyDeleted() {
        // given
        Deck deck = repository.save(getDeck(owner, 1));

        // when
        repository.deleteDeckById(deck.getId());

        // then
        Optional<Deck> deletedDeck = deckRepository.findDeckById(deck.getId());
        assertThat(deletedDeck).isNotNull();
        assertThat(deletedDeck.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteDeckWithWrongId_thenNothingHappened() {
        // given
        List<Deck> decks = IntStream.range(0, 10).mapToObj(i -> getDeck(owner, i))
                .map(repository::save).toList();

        // when
        repository.deleteDeckById(-1);

        // then
        List<Deck> storedDecks = deckRepository.findAll();
        assertThat(storedDecks.size()).isEqualTo(decks.size());
        IntStream.range(0, 10).forEach(
                i -> assertThat(storedDecks.get(i)).isEqualTo(decks.get(i))
        );
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @ValueSource(ints = {0, 1, 5, 10})
    void whenFindAllDecksWithManyDecksStored_thenAllDecksHaveReturned(int count) {
        // given
        List<Deck> decks = IntStream.range(0, count).mapToObj(i -> getDeck(owner, i))
                .map(repository::save).toList();

        // when
        List<Deck> storedDecks = repository.findAll();

        // then
        assertThat(storedDecks.size()).isEqualTo(decks.size());
        IntStream.range(0, count).forEach(
                i -> assertThat(storedDecks.get(i)).isEqualTo(decks.get(i))
        );
    }
}
