package com.github.bondarevv23.memorizer.server.service.integration;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.model.User;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.server.service.DeckService;
import com.github.bondarevv23.memorizer.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.server.util.TestUtil.getDeckRequest;
import static org.assertj.core.api.Java6Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "app.database-access-type=jdbc")
public class UserServiceIntegrationTest extends IntegrationEnvironment {
    @Autowired
    private UserService userService;

    @Autowired
    private DeckService deckService;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Rollback
    void whenDeleteUserById_thenDeleteAllHisDecksToo() {
        // given
        User user = userService.addNewUser(1L);
        deckService.addNewDeck(getDeckRequest(user, 1));
        deckService.addNewDeck(getDeckRequest(user, 2));

        // when
        userService.deleteUserById(user.getTgId());

        // then
        List<Deck> decks = deckRepository.findAll();
        assertThat(decks).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void whenAddNewDeck_thenOwnerHasThisDeck() {
        // given
        User user = userService.addNewUser(1L);

        // when
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));

        // then
        User storedUser = userRepository.findUserByTgId(user.getTgId()).get();
        assertThat(storedUser.getDecks()).containsExactly(deck);
    }

    @Test
    @Transactional
    @Rollback
    void whenShareDecksWithUser_thenUserHaveThisDecks() {
        // given
        User user = userService.addNewUser(1L);

        // when
        List<Deck> decks = IntStream.range(1, 11).mapToObj(i -> getDeckRequest(user, i))
                .map(deckService::addNewDeck).toList();

        // then
        User storedUser = userRepository.findUserByTgId(user.getTgId()).get();
        assertThat(storedUser.getDecks()).containsExactlyElementsOf(decks);
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteDecksFromUser_thenUserHaveNotThisDecks() {
        // given
        User user = userService.addNewUser(1L);
        List<Deck> decks = IntStream.range(1, 11).mapToObj(i -> getDeckRequest(user, i))
                .map(deckService::addNewDeck).toList();

        // when
        decks.forEach(deck -> deckService.deleteDeckById(deck.getId()));

        // then
        User storedUser = userRepository.findUserByTgId(user.getTgId()).get();
        assertThat(storedUser.getDecks()).isEmpty();
    }
}
