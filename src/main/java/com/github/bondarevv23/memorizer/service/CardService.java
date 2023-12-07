package com.github.bondarevv23.memorizer.service;

import com.github.bondarevv23.memorizer.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.model.Card;
import com.github.bondarevv23.memorizer.model.CardRequest;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.SideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
