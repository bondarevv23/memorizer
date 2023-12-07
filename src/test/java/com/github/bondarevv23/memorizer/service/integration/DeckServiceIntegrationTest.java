package com.github.bondarevv23.memorizer.service.integration;

import com.github.bondarevv23.memorizer.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.model.Card;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.SideRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.service.CardService;
import com.github.bondarevv23.memorizer.service.DeckService;
import com.github.bondarevv23.memorizer.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.util.TestUtil.getCardRequest;
import static com.github.bondarevv23.memorizer.util.TestUtil.getDeckRequest;
import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "app.database-access-type=jdbc")
public class DeckServiceIntegrationTest extends IntegrationEnvironment {
    @Autowired
    private DeckService deckService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SideRepository sideRepository;

    @Autowired
    private UserService userService;

    private User owner;

    @BeforeAll
    void initOwner() {
        owner = userRepository.save(User.builder().tgId(1L).build());
    }

    @AfterAll
    void deleteOwner() {
        userService.deleteUserById(owner.getTgId());
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewDeck_thenShareDeckWithOwner() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(owner, 1));

        // when
        Optional<User> storedOwner = userRepository.findUserByTgId(owner.getTgId());

        // then
        assertThat(storedOwner.isPresent()).isTrue();
        assertThat(storedOwner.get().getDecks()).containsExactly(deck);
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteDeckById_thenAllCardsAndSidesDeletesToo() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(owner, 1));
        IntStream.range(0, 3)
                .mapToObj(i -> getCardRequest(deck, i))
                .forEach(cardService::addNewCard);

        // when
        deckService.deleteDeckById(deck.getId());

        // then
        assertThat(cardRepository.findAll()).isEmpty();
        assertThat(sideRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void whenGerRandomCardFromDeckManyTimes_thenReturnedValuesAreDifferent() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(owner, 1));
        Card card1 = cardService.addNewCard(getCardRequest(deck, 1));
        Card card2 = cardService.addNewCard(getCardRequest(deck, 1));

        // when
        Set<Card> cards = IntStream.range(0, 100).mapToObj(i -> deckService.getRandomCardFromDeck(deck.getId()))
                .collect(Collectors.toSet());

        // then
        assertThat(cards.size()).isEqualTo(2);
        assertThat(cards).contains(card1, card2);
    }
}
