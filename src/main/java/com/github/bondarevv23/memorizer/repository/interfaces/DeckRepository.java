package com.github.bondarevv23.memorizer.repository.interfaces;

import com.github.bondarevv23.memorizer.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {

    void deleteDeckById(Integer id);

    Optional<Deck> findDeckById(Integer id);

    Deck save(Deck deck);

    List<Deck> findAll();
}
