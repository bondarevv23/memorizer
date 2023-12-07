package com.github.bondarevv23.memorizer.service.integration;

import com.github.bondarevv23.memorizer.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.model.*;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.SideRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.service.CardService;
import com.github.bondarevv23.memorizer.service.DeckService;
import com.github.bondarevv23.memorizer.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static com.github.bondarevv23.memorizer.util.TestUtil.getCardRequest;
import static com.github.bondarevv23.memorizer.util.TestUtil.getUpdateCardRequest;
import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "app.database-access-type=jdbc")
public class CardServiceIntegrationTest extends IntegrationEnvironment {
    @Autowired
    private CardService service;

    @Autowired
    private SideRepository sideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DeckService deckService;

    private Deck deck;

    @BeforeAll
    void initDeck() {
        User owner = userRepository.save(User.builder().tgId(1L).build());
        deck = deckRepository.save(Deck.builder()
                .owner(owner.getTgId())
                .title("deck")
                .build());
    }

    @AfterAll
    void deleteDeck() {
        deckService.deleteDeckById(deck.getId());
        userService.deleteUserById(deck.getOwner());
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewCard_thenAddCardAndTwoSides() {
        // given
        CardRequest cardRequest = getCardRequest(deck, 1);

        // when
        Card card = service.addNewCard(cardRequest);

        // then
        assertThat(card.getDeck()).isEqualTo(deck);
        assertThat(sideRepository.findAll()).contains(card.getAnswer(), card.getQuestion());
        assertThat(sideRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteCardById_thenSidesRemove() {
        // given
        Card card = service.addNewCard(getCardRequest(deck, 1));

        // when
        service.deleteCardById(card.getId());

        // then
        assertThat(sideRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void whenUpdateCardById_thenSidesUpdate() {
        // given
        Card card = service.addNewCard(getCardRequest(deck, 1));

        // when
        service.updateCardById(card.getId(), getUpdateCardRequest(deck, 1));

        // then
        Side question = sideRepository.findSideById(card.getQuestion().getId()).get();
        Side answer = sideRepository.findSideById(card.getAnswer().getId()).get();
        assertThat(question.getTitle()).isEqualTo("question update 1");
        assertThat(answer.getTitle()).isEqualTo("answer update 1");
    }

    @Test
    @Transactional
    @Rollback
    void whenGetCardByRightId_thenCardAndSidesReturns() {
        // given
        Card card = service.addNewCard(getCardRequest(deck, 1));

        // when
        Card cardById = service.getCardById(card.getId());

        // then
        assertThat(cardById).isEqualToComparingFieldByFieldRecursively(card);
    }
}
