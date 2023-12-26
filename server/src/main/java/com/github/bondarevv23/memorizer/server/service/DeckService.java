package com.github.bondarevv23.memorizer.server.service;

import com.github.bondarevv23.memorizer.server.controller.exception.ConflictException;
import com.github.bondarevv23.memorizer.server.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.server.model.Card;
import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.model.generated.DeckRequest;
import com.github.bondarevv23.memorizer.server.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final DeckMapper mapper;

    public Deck addNewDeck(DeckRequest deckRequest) {
        userRepository.findUserByTgId(deckRequest.getOwner()).orElseThrow(NotFoundException::new);
        Deck deck = mapper.deckRequestToDeck(deckRequest);
        Deck savedDeck = deckRepository.save(deck);
        userRepository.shareDeckWithUser(savedDeck.getOwner(), savedDeck.getId());
        return savedDeck;
    }

    public void deleteDeckById(Integer id) {
        Deck deck = deckRepository.findDeckById(id).orElseThrow(NotFoundException::new);
        cardRepository.findCardsByDeckId(deck.getId()).stream()
                .map(Card::getId).forEach(cardRepository::deleteCardById);
        deckRepository.deleteDeckById(id);
    }

    public Deck getDeckById(Integer id) {
        return deckRepository.findDeckById(id).orElseThrow(NotFoundException::new);
    }

    public Card getRandomCardFromDeck(Integer deckId) {
        deckRepository.findDeckById(deckId).orElseThrow(NotFoundException::new);
        return cardRepository.getRandomFromDeck(deckId).orElseThrow(ConflictException::new);
    }

    public void updateDeckById(Integer id, DeckRequest deckRequest) {
        deckRepository.findDeckById(id).orElseThrow(NotFoundException::new);
        Deck deck = mapper.deckRequestToDeck(deckRequest);
        deck.setId(id);
        deckRepository.save(deck);
    }

    public List<Card> getAllCardsFromDeck(Integer deckId) {
        return cardRepository.findCardsByDeckId(deckId);
    }
}
