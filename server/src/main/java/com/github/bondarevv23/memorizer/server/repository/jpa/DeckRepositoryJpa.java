package com.github.bondarevv23.memorizer.server.repository.jpa;

import com.github.bondarevv23.memorizer.server.model.Deck;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepositoryJpa extends JpaRepository<Deck, Integer> ,DeckRepository {

}
