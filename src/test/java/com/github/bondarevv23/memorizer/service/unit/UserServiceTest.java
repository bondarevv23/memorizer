package com.github.bondarevv23.memorizer.service.unit;

import com.github.bondarevv23.memorizer.controller.exception.ConflictException;
import com.github.bondarevv23.memorizer.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.service.DeckService;
import com.github.bondarevv23.memorizer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final DeckRepository deckRepository = mock(DeckRepository.class);
    private final DeckService deckService = Mockito.mock(DeckService.class);
    private final UserService service = new UserService(userRepository, deckRepository, deckService);

    @BeforeEach
    void init() {
        when(userRepository.findUserByTgId(-1L)).thenReturn(Optional.empty());
        when(userRepository.findUserByTgId(1L)).thenReturn(Optional.of(getFullUser(1)));
        when(userRepository.findUserByTgId(2L)).thenReturn(Optional.empty());
        when(userRepository.save(getUser(2))).thenReturn(getFullUser(2));
        doNothing().when(userRepository).deleteUserByTgId(1L);
        doNothing().when(userRepository).removeDeckFromUser(1L, 2);
        when(deckRepository.findDeckById(-1)).thenReturn(Optional.empty());
        when(deckRepository.findDeckById(1)).thenReturn(Optional.of(getDeck(1)));
        when(deckRepository.findDeckById(2)).thenReturn(Optional.of(getDeck(2)));
        doNothing().when(userRepository).shareDeckWithUser(1L, 1);
        doAnswer(invocation -> {
            throw new DataIntegrityViolationException("exception!");
        }).when(userRepository).shareDeckWithUser(1L, 2);

    }

    @Test
    void whenAddNewUserWithNewIde_thenUserHasSuccessfullyAddedAndReturned() {
        // given
        long id = 2;

        // when
        User user = service.addNewUser(id);

        // then
        assertThat(user).isEqualTo(getFullUser(2));
        verify(userRepository, times(1)).save(getUser(2));
    }

    @Test
    void whenAddNewUserWithExistedId_thenConflictExceptionThrows() {
        // given
        long id = 1;

        // when

        // then
        assertThatThrownBy(() -> service.addNewUser(id)).isInstanceOf(ConflictException.class);
    }

    @Test
    void whenDeleteUserById_thenNothingHappensAndRepositoryCallsOnce() {
        // given
        long id = 1;

        // when
        service.deleteUserById(id);

        // then
        verify(userRepository, times(1)).deleteUserByTgId(id);
    }

    @Test
    void whenGetDecksByRightUserId_thenDecksReturned() {
        // given
        long userId = 1L;

        // when
        List<Deck> decksByUserId = service.getDecksByUserId(1L);

        // then
        assertThat(decksByUserId).containsExactlyElementsOf(getFullUser((int) userId).getDecks());
    }

    @Test
    void whenGetDecksByWrongUserId_thenNotFoundExceptionThrows() {
        // given
        long userId = -1L;

        // when

        // then
        assertThatThrownBy(() -> service.getDecksByUserId(userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenRemoveDeckFromUser_thenRepositoryCalled() {
        // given
        long userId = 1L;
        int deckId = 2;

        // when
        service.removeDeckFromUser(userId, deckId);

        // then
        verify(userRepository, times(1)).removeDeckFromUser(userId, deckId);
    }

    @Test
    void whenRemoveDeckFromUserWithWrongUserIdOrDeckId_thenNotFoundExceptionThrows() {
        // given
        int wrongDeckId = -1;
        int deckId = 1;
        long wrongUserId = -1L;
        long userId = 1L;

        // when

        // then
        assertThatThrownBy(() -> service.removeDeckFromUser(userId, wrongDeckId))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.removeDeckFromUser(wrongUserId, deckId))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.removeDeckFromUser(wrongUserId, wrongDeckId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenRemoveDeckFromOwner_thenConflictExceptionThrows() {
        // given
        long userId = 1L;
        int deckId = 1;

        // when

        // then
        assertThatThrownBy(() -> service.removeDeckFromUser(userId, deckId)).isInstanceOf(ConflictException.class);
    }

    @Test
    void whenShareDeckWithUser_thenUserRepositoryCalled() {
        // given
        long userId = 1L;
        int deckId = 1;

        // when
        service.shareDeckWithUser(userId, deckId);

        // then
        verify(userRepository, times(1)).shareDeckWithUser(userId, deckId);
    }

    @Test
    void whenShareDeckWithUserWithWrongUserIdOrDeckId_thenNotFoundExceptionThrows() {
        // given
        int wrongDeckId = -1;
        int deckId = 1;
        long wrongUserId = -1L;
        long userId = 1L;

        // when

        // then
        assertThatThrownBy(() -> service.shareDeckWithUser(userId, wrongDeckId))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.shareDeckWithUser(wrongUserId, deckId))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.shareDeckWithUser(wrongUserId, wrongDeckId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenShareDeckWithUserWithButUserAlreadyHasThisDeck_thenConflictException() {
        // given
        int deckId = 2;
        long userId = 1L;

        // when

        // then
        assertThatThrownBy(() -> service.shareDeckWithUser(userId, deckId)).isInstanceOf(ConflictException.class);
    }

    private Deck getDeck(int i) {
        return Deck.builder()
                .id(i)
                .title("deck " + i)
                .owner((long) i)
                .build();
    }

    private User getUser(int i) {
        return User.builder()
                .tgId((long) i)
                .build();
    }

    private User getFullUser(int i) {
        return User.builder()
                .tgId((long) i)
                .decks(Collections.emptyList())
                .build();
    }
}
