package com.github.bondarevv23.memorizer.server.service.unit;

import com.github.bondarevv23.memorizer.server.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.server.model.*;
import com.github.bondarevv23.memorizer.server.model.generated.CardRequest;
import com.github.bondarevv23.memorizer.server.model.generated.SideDTO;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CardServiceTest {

    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final DeckRepository deckRepository = Mockito.mock(DeckRepository.class);
    private final CardMapper mapper = Mappers.getMapper(CardMapper.class);
    private final CardService service = new CardService(cardRepository, deckRepository, mapper);

    @BeforeEach
    public void mock() {
        IntStream.range(1, 6).forEach(
                i -> {
                    when(cardRepository.save(getCard(i))).thenReturn(getCardWithId(i));
                    when(cardRepository.save(getCardWithId(i))).thenReturn(getCardWithId(i));
                    doNothing().when(cardRepository).deleteCardById(i);
                    when(cardRepository.findCardById(i)).thenReturn(Optional.of(getCardWithId(i)));
                    when(cardRepository.save(getCardUpdateWithId(i))).thenReturn(getCardUpdateWithId(i));
                    when(cardRepository.save(getCardWithDeck(i))).thenReturn(getCardWithId(i));
                    when(deckRepository.findDeckById(i)).thenReturn(Optional.of(getDeck(1)));
                }
        );
        IntStream.range(-5, 0).forEach(
                i -> {
                    when(cardRepository.findCardById(i)).thenReturn(Optional.empty());
                    when(cardRepository.save(getCard(i))).thenReturn(getCardWithId(i));
                    doNothing().when(cardRepository).deleteCardById(i);
                }
        );
    }

    @Test
    void whenAddNewCardWithRightRequest_thenReturnCard() {
        // given
        CardRequest request = getCardRequest(1);

        // when
        Card card = service.addNewCard(request);

        // then
        assertThat(card).isEqualTo(getCardWithId(1));
    }

    @Test
    void whenDeleteCardByIdWithRightId_thenRepositoryCalledOnce() {
        // given
        Integer cardId = 1;

        // when
        service.deleteCardById(cardId);

        // then
        verify(cardRepository, times(1)).deleteCardById(1);
        verify(cardRepository, times(1)).findCardById(1);
    }

    @Test
    void whenDeleteCardByWrongId_thenNotFoundExceptionThrows() {
        // given
        Integer cardId = -1;

        // when

        // then
        assertThatThrownBy(() -> service.deleteCardById(cardId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenGetCardByIdWithRightId_thenReturnCard() {
        // given
        int id = 1;

        // when
        Card card = service.getCardById(id);

        // then
        assertThat(card).isNotNull();
        assertThat(card).isEqualTo(getCardWithId(id));
    }

    @Test
    void whenFindCardByIdWithWrongId_thenNotFoundExceptionThrows() {
        // given
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.getCardById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void whenUpdateCardById_thenSaveUpdatedCard() {
        // given
        CardRequest update = getCardRequestUpdate(1);

        // when
        service.updateCardById(1, update);

        // then
        verify(cardRepository, times(1)).save(getCardUpdateWithId(1));
    }

    @Test
    void whenUpdateCardWithWrongId_thenThrowNotFoundException() {
        // given
        int id = -1;

        // when

        // then
        assertThatThrownBy(() -> service.updateCardById(id, getCardRequest(id)))
                .isInstanceOf(NotFoundException.class);
    }

    private CardRequest getCardRequestUpdate(int i) {
        return CardRequest.builder()
                .question(getQuestionDTOUpdate(i))
                .answer(getAnswerDTOUpdate(i))
                .deckId(i)
                .build();
    }

    private SideDTO getQuestionDTOUpdate(int i) {
        return getSideDTO("question update", i);
    }

    private SideDTO getAnswerDTOUpdate(int i) {
        return getSideDTO("answer update", i);
    }

    private CardRequest getCardRequest(int i) {
        return CardRequest.builder()
                .question(getQuestionDTO(i))
                .answer(getAnswerDTO(i))
                .deckId(i)
                .build();
    }

    private SideDTO getQuestionDTO(int i) {
        return getSideDTO("question", i);
    }

    private SideDTO getAnswerDTO(int i) {
        return getSideDTO("answer", i);
    }

    private SideDTO getSideDTO(String s, int i) {
        return SideDTO.builder()
                .title(s + " " + i)
                .text("text")
                .imageLink("imageLink")
                .build();
    }

    private Deck getDeck(int i) {
        return Deck.builder()
                .owner((long) i)
                .title("deck " + i)
                .build();
    }

    private Card getCardWithDeck(int i) {
        Card card = mapper.cardRequestToCard(getCardRequest(i));
        card.setDeck(getDeck(i));
        return card;
    }

    private Card getCardUpdateWithId(int i) {
        Card card = mapper.cardRequestToCard(getCardRequestUpdate(i));
        card.setDeck(getDeck(i));
        card.setId(i);
        card.getQuestion().setId(2 * i);
        card.getAnswer().setId(2 * i + 1);
        return card;
    }

    private Card getCard(int i) {
        return mapper.cardRequestToCard(getCardRequest(i));
    }

    private Card getCardWithId(int i) {
        Card card = mapper.cardRequestToCard(getCardRequest(i));
        card.setId(i);
        card.setDeck(getDeck(i));
        card.getQuestion().setId(2 * i);
        card.getAnswer().setId(2 * i + 1);
        return card;
    }
}
