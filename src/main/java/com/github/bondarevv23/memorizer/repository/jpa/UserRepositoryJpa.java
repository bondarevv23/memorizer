package com.github.bondarevv23.memorizer.repository.jpa;

import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface UserRepositoryJpa extends JpaRepository<User, Long>, UserRepository {

    @Modifying
    @Query(value = "delete from user_deck where user_tg_id = ? and deck_id = ?", nativeQuery = true)
    void removeDeckFromUser(Long userId, Integer deckId);

    @Modifying
    @Query(value = "insert into user_deck (user_tg_id, deck_id) values (?, ?)", nativeQuery = true)
    void shareDeckWithUser(Long userId, Integer deckId);
}
