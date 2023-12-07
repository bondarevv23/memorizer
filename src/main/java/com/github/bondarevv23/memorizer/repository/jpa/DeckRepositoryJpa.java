package com.github.bondarevv23.memorizer.repository.jpa;

import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DeckRepositoryJpa extends JpaRepository<Deck, Integer> ,DeckRepository {

}
