package com.github.bondarevv23.memorizer.server.util;

import com.github.bondarevv23.memorizer.server.model.*;
import com.github.bondarevv23.memorizer.server.model.generated.CardDTO;
import com.github.bondarevv23.memorizer.server.model.generated.CardRequest;
import com.github.bondarevv23.memorizer.server.model.generated.DeckRequest;
import com.github.bondarevv23.memorizer.server.model.generated.SideDTO;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import org.mapstruct.factory.Mappers;

public class TestUtil {
    private static final CardMapper CARD_MAPPER = Mappers.getMapper(CardMapper.class);

    public static DeckRequest getDeckRequest(User user, int i) {
        return DeckRequest.builder()
                .owner(user.getTgId())
                .title("deck " + i)
                .build();
    }

    public static CardDTO getCardDTO(Deck deck, int i) {
        CardRequest request = getCardRequest(deck, i);
        Card card = CARD_MAPPER.cardRequestToCard(request);
        return CARD_MAPPER.cardToCardDTO(card);
    }

    public static CardRequest getCardRequest(Deck deck, int i) {
        return CardRequest.builder()
                .deckId(deck.getId())
                .question(getSideDto("question", i))
                .answer(getSideDto("answer", i))
                .build();
    }

    public static SideDTO getSideDto(String s, int i) {
        return SideDTO.builder()
                .title(s + " " + i)
                .text("text")
                .imageLink("imageLink")
                .build();
    }

    public static Card getCard(Deck deck, int i) {
        return Card.builder()
                .deck(deck)
                .question(getQuestion(i))
                .answer(getAnswer(i))
                .build();
    }

    public static Side getQuestion(int i) {
        return getSide(i, "question");
    }

    public static Side getAnswer(int i) {
        return getSide(i, "answer");
    }

    public static Side getSide(int i, String s) {
        return Side.builder()
                .title(s + " " + i)
                .text("text")
                .imageLink("imageLink")
                .build();
    }

    public static Side getSide(int i) {
        return getSide(i, "side");
    }

    public static void updateSide(Side side) {
        side.setText("new text");
        side.setImageLink("new imageLink");
    }

    public static void updateCard(Card card) {
        updateSide(card.getQuestion());
        updateSide(card.getAnswer());
    }

    public static Deck getDeck(Long owner, int i) {
        return Deck.builder()
                .owner(owner)
                .title("deck " + i)
                .build();
    }

    public static Deck getDeck(User owner, int i) {
        return getDeck(owner.getTgId(), i);
    }

    public static void updateDeck(Deck deck) {
        deck.setTitle("new deck");
    }

    public static User getUser(int i){
        return User.builder()
                .tgId((long) i)
                .build();
    }

    public static CardRequest getUpdateCardRequest(Deck deck, int i) {
        return CardRequest.builder()
                .deckId(deck.getId())
                .question(getUpdateQuestionDTO(i))
                .answer(getUpdateAnswerDTO(i))
                .build();
    }

    public static SideDTO getUpdateQuestionDTO(int i) {
        return getSideDTO("question update", i);
    }

    public static SideDTO getUpdateAnswerDTO(int i) {
        return getSideDTO("answer update", i);
    }

    public static SideDTO getSideDTO(String s, int i) {
        return SideDTO.builder()
                .title(s + " " + i)
                .text("text")
                .imageLink("imageLink")
                .build();
    }
}
