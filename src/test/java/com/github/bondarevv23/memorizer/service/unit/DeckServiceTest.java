package com.github.bondarevv23.memorizer.service.unit;

import com.github.bondarevv23.memorizer.controller.exception.ConflictException;
import com.github.bondarevv23.memorizer.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.model.Card;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.DeckRequest;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
public class DeckServiceTest {
    private final DeckRepository deckRepository = Mockito.mock(DeckRepository.class);
    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final DeckMapper mapper = Mappers.getMapper(DeckMapper.class);
    private final DeckService service = new DeckService(
            deckRepository,
            cardRepository,
            userRepository,
            mapper
    );

    @BeforeEach
    void mock() {
        when(userRepository.findUserByTgId(1L)).thenReturn(Optional.of(getUser(1)));
        when(userRepository.findUserByTgId(-1L)).thenReturn(Optional.empty());
        when(deckRepository.save(getDeck(1))).thenReturn(getDeckWithId(1));
        doNothing().when(userRepository).shareDeckWithUser(1L, 1);
        when(deckRepository.findDeckById(1)).thenReturn(Optional.of(getDeckWithId(1)));
        when(deckRepository.findDeckById(2)).thenReturn(Optional.of(getDeckWithId(2)));
        when(deckRepository.findDeckById(-1)).thenReturn(Optional.empty());
        doNothing().when(deckRepository).deleteDeckById(1);
        when(cardRepository.getRandomFromDeck(1)).thenReturn(Optional.of(getCard(1)));
        when(cardRepository.getRandomFromDeck(2)).thenReturn(Optional.empty());
    }

    @Test
    void whenAddNewDeckWithRightRequest_thenReturnsNewDeckSuccessfully() {
        // given
        DeckRequest request = getDeckRequest(1L, 1);

        // when
        Deck deck = service.addNewDeck(request);

        // then
        assertThat(deck).isNotNull();
        assertThat(deck).isEqualTo(getDeckWithId(1));
    }

    @Test
    void whenAddNewDeckWithWrongOwnerId_thenNotFoundException() {
        // given
        DeckRequest request = getDeckRequest(-1L, 1);

        // when

        // then
        assertThatThrownBy(() -> service.addNewDeck(request)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenDeleteDeckByIdWithRightId_thenDeleteDeckByIdCallsOnce() {
        // given
        int id = 1;

        // when
        service.deleteDeckById(id);

        // then
        verify(deckRepository, times(1)).deleteDeckById(id);
    }

    @Test
    void whenDeleteDeckByIdWithWrongId_thenNotFoundException() {
        // given
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.deleteDeckById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenGetDeckById_thenFindDeckByIdCallsOnce() {
        // given
        int id = 1;

        // when
        service.getDeckById(id);

        // then
        verify(deckRepository, times(1)).findDeckById(id);
    }

    @Test
    void whenGetDeckByWrongId_thenNotFoundExceptionThrows() {
        // given
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.getDeckById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenGetRandomCardFromNotEmptyDeck_thenReturnsCard() {
        // given
        int id = 1;

        // when
        Card card = service.getRandomCardFromDeck(id);

        // then
        assertThat(card).isEqualTo(getCard(id));
    }

    @Test
    void whenGetRandomCardFromEmptyDeck_thenConflictException() {
        // given
        int id = 2;

        // when

        // then
        assertThatThrownBy(() -> service.getRandomCardFromDeck(id)).isInstanceOf(ConflictException.class);
    }

    @Test
    void whenGetRandomCardFromWrongDeckId_thenNotFoundException() {
        // given
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.getRandomCardFromDeck(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenUpdateDeckById_thenSaveCallsOnNewDeck() {
        // given
        DeckRequest request = getDeckRequest(1L, 1);
        int id = 1;

        // when
        service.updateDeckById(id, request);

        // then
        verify(deckRepository, times(1)).save(getDeckWithId(1));
    }

    @Test
    void whenUpdateDeckByWrongId_thenThrowsNotFoundException() {
        // given
        DeckRequest request = getDeckRequest(-1L, -1);
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.updateDeckById(id, request)).isInstanceOf(NotFoundException.class);
    }

    private DeckRequest getDeckRequest(long owner, int i) {
        return DeckRequest.builder()
                .owner(owner)
                .title("deck " + i)
                .build();
    }

    private Deck getDeck(int i) {
        return mapper.deckRequestToDeck(getDeckRequest(i, i));
    }

    private Deck getDeckWithId(int i) {
        return Deck.builder()
                .title("deck " + i)
                .owner((long) i)
                .id(i)
                .build();
    }

    private User getUser(int i) {
        return User.builder()
                .tgId((long) i)
                .decks(List.of(getDeckWithId(2 *i), getDeckWithId(2 * i + 1)))
                .build();
    }

    private Card getCard(int i) {
        return Card.builder()
                .id(i)
                .build();
    }
}
