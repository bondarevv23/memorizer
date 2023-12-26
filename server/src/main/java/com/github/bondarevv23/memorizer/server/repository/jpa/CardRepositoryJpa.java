package com.github.bondarevv23.memorizer.server.repository.jpa;

import com.github.bondarevv23.memorizer.server.model.Card;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CardRepositoryJpa extends JpaRepository<Card, Integer>, CardRepository {

    @Query(value = "select * from card where deck_id = ? order by random() limit 1", nativeQuery = true)
    Optional<Card> getRandomFromDeck(Integer id);
}
