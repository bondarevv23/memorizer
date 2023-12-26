package com.github.bondarevv23.memorizer.server.repository.interfaces;

import com.github.bondarevv23.memorizer.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findUserByTgId(Long tgId);

    List<User> findAll();

    void deleteUserByTgId(Long tgId);

    void removeDeckFromUser(Long userId, Integer deckId);

    void shareDeckWithUser(Long userId, Integer deckId);
}
