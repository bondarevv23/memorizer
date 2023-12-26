package com.github.bondarevv23.memorizer.server.repository;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.model.User;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.server.util.TestUtil;
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

import static com.github.bondarevv23.memorizer.server.util.TestUtil.getDeck;
import static com.github.bondarevv23.memorizer.server.util.TestUtil.getUser;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

@SpringBootTest(properties = "app.database-access-type=jdbc")
@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryJdbcTest extends IntegrationEnvironment {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Test
    @Transactional
    @Rollback
    void whenAddNewUser_thenFindAllContainsOnlyThisUser() {
        // given
        User user = getUser(1);

        // when
        User saved = repository.save(user);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0)).isEqualToComparingOnlyGivenFields(saved, "tgId");
    }

    @Test
    @Transactional
    @Rollback
    void whenFindUserByExistingId_thenUserHasFound() {
        // given
        User user = repository.save(getUser(1));

        // when
        Optional<User> founded = repository.findUserByTgId(user.getTgId());

        // then
        assertThat(founded).isNotNull();
        assertThat(founded.isPresent()).isTrue();
        assertThat(founded.get()).isEqualToComparingFieldByField(user);
    }

    @Test
    @Transactional
    @Rollback
    void whenFindUserByWrongId_thenEmptyOptionalHasReturned() {
        // given
        repository.save(getUser(1));

        // when
        Optional<User> founded = repository.findUserByTgId(-1L);

        // then
        assertThat(founded).isNotNull();
        assertThat(founded.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10})
    @Transactional
    @Rollback
    void whenSomeUsersExists_thenFindAllReturnsAllOfThem(int count) {
        // given
        List<User> users = IntStream.range(0, count).mapToObj(TestUtil::getUser).map(repository::save).toList();

        // when
        List<User> stored = repository.findAll();

        // then
        assertThat(stored).containsExactlyElementsOf(users);
    }

    @Test
    @Transactional
    @Rollback
    void whenUserExists_thenDeleteUserByIdDeleteIt() {
        // given
        User user = repository.save(getUser(1));

        // when
        repository.deleteUserByTgId(user.getTgId());

        // then
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void whenDeleteUserByWrongId_thenNothingHappens() {
        // given
        User user = userRepository.save(getUser(1));

        // when
        repository.deleteUserByTgId(-1L);

        // then
        Optional<User> stored = userRepository.findUserByTgId(user.getTgId());
        assertThat(stored).isNotNull();
        assertThat(stored.isPresent()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void whenShareDeckWithUser_thenUserHaveThisDeck() {
        // given
        User user1 = userRepository.save(getUser(1));
        User user2 = userRepository.save(getUser(2));
        Deck deck1 = deckRepository.save(getDeck(user1, 1));
        userRepository.shareDeckWithUser(user1.getTgId(), deck1.getId());
        Deck deck2 = deckRepository.save(getDeck(user1, 2));
        userRepository.shareDeckWithUser(user1.getTgId(), deck2.getId());

        // when
        repository.shareDeckWithUser(user2.getTgId(), deck1.getId());
        repository.shareDeckWithUser(user2.getTgId(), deck2.getId());

        // then
        User user1Stored = repository.findUserByTgId(user1.getTgId()).get();
        assertThat(user1Stored.getDecks()).containsExactly(deck1, deck2);
    }

    @Test
    @Transactional
    @Rollback
    void whenShareDeckWithWrongUserIdOrWrongDeckId_thenDataAccessExceptionThrows() {
        // given
        User user = userRepository.save(getUser(1));
        Deck deck = deckRepository.save(getDeck(user, 1));

        // when

        // then
        assertThatThrownBy(() -> repository.shareDeckWithUser(user.getTgId(), -1))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> repository.shareDeckWithUser(-1L, deck.getId()))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> repository.shareDeckWithUser(-1L, -1))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    void whenRemoveDeckFromUser_thenDeckHasDeletedSuccessfully() {
        // given
        User user = userRepository.save(getUser(1));
        Deck deck1 = deckRepository.save(getDeck(user, 1));
        userRepository.shareDeckWithUser(user.getTgId(), deck1.getId());
        Deck deck2 = deckRepository.save(getDeck(user, 2));
        userRepository.shareDeckWithUser(user.getTgId(), deck2.getId());

        // when
        repository.removeDeckFromUser(user.getTgId(), deck2.getId());

        // then
        User stored = repository.findUserByTgId(user.getTgId()).get();
        assertThat(stored.getDecks()).containsExactly(deck1);
    }

    @Test
    @Transactional
    @Rollback
    void whenRemoveDeckFromUserWrongUserIdOrWrongDeckId_thenNothingHappens() {
        // given
        User user1 = userRepository.save(getUser(1));
        User user2 = userRepository.save(getUser(2));
        Deck deck1 = deckRepository.save(getDeck(user1, 1));
        userRepository.shareDeckWithUser(user1.getTgId(), deck1.getId());
        Deck deck2 = deckRepository.save(getDeck(user1, 2));
        userRepository.shareDeckWithUser(user1.getTgId(), deck2.getId());
        Deck deck3 = deckRepository.save(getDeck(user2, 3));
        userRepository.shareDeckWithUser(user2.getTgId(), deck3.getId());

        // when
        repository.removeDeckFromUser(user1.getTgId(), -1);
        repository.removeDeckFromUser(user2.getTgId(), -1);
        repository.removeDeckFromUser(-1L, deck1.getId());
        repository.removeDeckFromUser(-1L, deck2.getId());
        repository.removeDeckFromUser(-1L, deck3.getId());
        repository.removeDeckFromUser(-1L, -1);

        // then
        User storedUser1 = repository.findUserByTgId(user1.getTgId()).get();
        User storedUser2 = repository.findUserByTgId(user2.getTgId()).get();
        assertThat(storedUser1.getDecks()).containsExactly(deck1, deck2);
        assertThat(storedUser2.getDecks()).containsExactly(deck3);
    }
}
