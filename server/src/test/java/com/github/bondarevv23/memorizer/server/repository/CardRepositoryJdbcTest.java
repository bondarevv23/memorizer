package com.github.bondarevv23.memorizer.server.repository;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.Card;
import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.model.User;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.server.util.TestUtil.getCard;
import static com.github.bondarevv23.memorizer.server.util.TestUtil.updateCard;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CardRepositoryJdbcTest extends IntegrationEnvironment {

    @Autowired
    private CardRepository repository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private User owner;
    private Deck firstDeck;
    private Deck secondDeck;
    private Deck wrongDeck;

    @BeforeAll
    public void createDecks() {
        owner = userRepository.save(User.builder().tgId(1L).build());
        firstDeck = deckRepository.save(Deck.builder()
                .owner(owner.getTgId())
                .title("deck 1")
                .build());
        secondDeck = deckRepository.save(Deck.builder()
                .owner(owner.getTgId())
                .title("deck 2")
                .build());
        wrongDeck = Deck.builder()
                .id(-1)
                .owner(owner.getTgId())
                .title("wrong deck 1")
                .build();
    }

    @AfterAll
    public void deleteDecks() {
        deckRepository.findAll().forEach(deck -> deckRepository.deleteDeckById(deck.getId()));
        userRepository.deleteUserByTgId(owner.getTgId());
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewCard_thenAddedSuccessfully() {
        // given
        Card card = getCard(firstDeck, 1);

        // when
        Card savedCard = repository.save(card);

        // then
        Optional<Card> storedCard = repository.findCardById(savedCard.getId());
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isPresent()).isTrue();
        assertThat(storedCard.get()).isEqualTo(savedCard);
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewCardWithWrongDeck_thenThrowDataAccessException() {
        // given
        Card card = getCard(wrongDeck, 1);

        // when

        // then
        assertThatThrownBy(() -> repository.save(card)).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    void whenUpdateCard_thenSuccessfullyUpdated() {
        // given
        Card card = repository.save(getCard(firstDeck, 1));
        updateCard(card);

        // when
        Card updatedCard = repository.save(card);

        // then
        Optional<Card> storedCard = cardRepository.findCardById(updatedCard.getId());
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isPresent()).isTrue();
        assertThat(storedCard.get()).isEqualTo(updatedCard);
    }

    @Test
    @Transactional
    @Rollback
    void whenUpdateCardToWrongDeck_thenThrowDataAccessException() {
        // given
        Card card = repository.save(getCard(firstDeck, 1));
        updateCard(card);
        card.setDeck(wrongDeck);

        // when

        // then
        assertThatThrownBy(() -> repository.save(card)).isInstanceOf(DataAccessException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 25})
    @Transactional
    @Rollback
    void whenSaveSomeCards_thenFindAllSuccessfully(int count) {
        // given
        List<Card> cards = IntStream.range(0, count).mapToObj(i -> getCard(firstDeck, i)).map(repository::save).toList();

        // when
        List<Card> storedCards = repository.findAll();

        // then
        IntStream.range(0, storedCards.size()).forEach(
                i -> assertThat(cards.get(i)).isEqualToComparingFieldByField(storedCards.get(i))
        );
    }

    @Test
    @Transactional
    @Rollback
    void whenFindCardByIdWithRightId_thenSuccess() {
        // given
        Card card = repository.save(getCard(firstDeck, 1));

        // when
        Optional<Card> storedCard = repository.findCardById(card.getId());

        // then
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isPresent()).isTrue();
        assertThat(storedCard.get()).isEqualTo(card);
    }

    @Test
    @Transactional
    @Rollback
    void whenFindSideByIdWithWrongId_thenEmptyOptional() {
        // given
        repository.save(getCard(firstDeck, 1));

        // when
        Optional<Card> storedCard = repository.findCardById(-1);

        // then
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteCardByIdWithRightId_thenDeleteSuccessfully() {
        // given
        Card card = repository.save(getCard(firstDeck, 1));

        // when
        repository.deleteCardById(card.getId());

        // then
        Optional<Card> storedCard = repository.findCardById(card.getId());
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteSideByIdWithWrongId_thenNothingHappened() {
        // given
        Card card = repository.save(getCard(firstDeck, 1));

        // when
        repository.deleteCardById(-1);

        // then
        Optional<Card> storedCard = repository.findCardById(card.getId());
        assertThat(storedCard).isNotNull();
        assertThat(storedCard.isPresent()).isTrue();
        assertThat(storedCard.get()).isEqualTo(card);
    }

    @ParameterizedTest
    @Transactional
    @Rollback
    @ValueSource(ints = {0, 1, 2, 5, 10})
    void whenFindCardsByDeckId_thenReturnedOnlyThatDeckCards(int count) {
        // given
        List<Card> first = IntStream.range(0, count).mapToObj(i -> getCard(firstDeck, i))
                .map(repository::save).toList();
        List<Card> second = IntStream.range(count, 2 * count).mapToObj(i -> getCard(secondDeck, i))
                .map(repository::save).toList();

        // when
        List<Card> storedFirst = repository.findCardsByDeckId(firstDeck.getId());
        List<Card> storedSecond = repository.findCardsByDeckId(secondDeck.getId());

        // then
        assertThat(storedFirst).isNotNull();
        assertThat(storedFirst.size()).isEqualTo(first.size());
        IntStream.range(0, storedFirst.size()).forEach(
                i -> assertThat(storedFirst.get(i)).isEqualTo(first.get(i))
        );
        assertThat(storedSecond.size()).isEqualTo(second.size());
        IntStream.range(0, storedSecond.size()).forEach(
                i -> assertThat(storedSecond.get(i)).isEqualTo(second.get(i))
        );
    }

    @Transactional
    @Rollback
    @Test
    void whenFindCardsByDeckIdWithWrongDeckId_thenReturnedEmptyList() {
        // given
        IntStream.range(0, 3).mapToObj(i -> getCard(firstDeck, i)).forEach(repository::save);

        // when
        List<Card> empty = repository.findCardsByDeckId(wrongDeck.getId());

        // then
        assertThat(empty).isNotNull();
        assertThat(empty.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @Repeat(10)
    @ValueSource(ints = {500})
    @Transactional
    @Rollback
    void whenGetRandomCardFromDeckWithManyCards_thenForManyRequestsThereAeAtLeastTwoDifferentCards(int count) {
        // given
        IntStream.range(0, count).mapToObj(i -> getCard(firstDeck, i)).forEach(repository::save);

        // when
        Set<Card> cards = IntStream.range(0, count)
                .mapToObj(i -> repository.getRandomFromDeck(firstDeck.getId()))
                .map(Optional::get)
                .collect(Collectors.toSet());

        // then
        assertThat(cards.size() > 1).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenGetRandomCardFromEmptyDeck_thenEmptyOptionalReturns() {
        // given
        IntStream.range(0, 10).mapToObj(i -> getCard(firstDeck, i)).forEach(repository::save);

        // when
        Optional<Card> random = repository.getRandomFromDeck(secondDeck.getId());

        // then
        assertThat(random).isNotNull();
        assertThat(random.isEmpty()).isTrue();
    }
}
