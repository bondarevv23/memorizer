package com.github.bondarevv23.memorizer.server.service;

import com.github.bondarevv23.memorizer.server.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.server.model.Card;
import com.github.bondarevv23.memorizer.server.model.generated.CardRequest;
import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final CardMapper mapper;

    public Card addNewCard(CardRequest cardRequest) {
        Deck deck = deckRepository.findDeckById(cardRequest.getDeckId()).orElseThrow(NotFoundException::new);
        Card card = mapper.cardRequestToCard(cardRequest);
        card.setDeck(deck);
        return cardRepository.save(card);
    }

    public void deleteCardById(Integer id) {
        cardRepository.findCardById(id).orElseThrow(NotFoundException::new);
        cardRepository.deleteCardById(id);
    }

    public Card getCardById(Integer id) {
        return cardRepository.findCardById(id).orElseThrow(NotFoundException::new);
    }

    public void updateCardById(Integer id, CardRequest cardRequest) {
        Card storedCard = cardRepository.findCardById(id).orElseThrow(NotFoundException::new);
        Card card = mapper.cardRequestToCard(cardRequest);
        card.setId(id);
        card.getQuestion().setId(storedCard.getQuestion().getId());
        card.getAnswer().setId(storedCard.getAnswer().getId());
        card.setDeck(storedCard.getDeck());
        cardRepository.save(card);
    }
}
