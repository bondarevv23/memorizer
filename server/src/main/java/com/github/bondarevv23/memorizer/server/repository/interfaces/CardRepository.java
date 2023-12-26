package com.github.bondarevv23.memorizer.server.repository.interfaces;

import com.github.bondarevv23.memorizer.server.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Optional<Card> findCardById(Integer id);

    void deleteCardById(Integer id);

    List<Card> findCardsByDeckId(Integer deckId);

    List<Card> findAll();

    Card save(Card card);

    Optional<Card> getRandomFromDeck(Integer id);
}
