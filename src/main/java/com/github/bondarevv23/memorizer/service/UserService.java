package com.github.bondarevv23.memorizer.service;

import com.github.bondarevv23.memorizer.controller.exception.ConflictException;
import com.github.bondarevv23.memorizer.controller.exception.NotFoundException;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final DeckService deckService;

    public User addNewUser(Long id) {
        userRepository.findUserByTgId(id).ifPresent(u -> {throw new ConflictException();});
        User user = User.builder().tgId(id).build();
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findUserByTgId(id).orElseThrow(NotFoundException::new);
        user.getDecks().stream().map(Deck::getId).forEach(deckService::deleteDeckById);
        userRepository.deleteUserByTgId(id);
    }

    public List<Deck> getDecksByUserId(Long id) {
        return userRepository.findUserByTgId(id).orElseThrow(NotFoundException::new).getDecks();
    }

    public void removeDeckFromUser(Long id, Integer deckId) {
        Deck deck = deckRepository.findDeckById(deckId).orElseThrow(NotFoundException::new);
        if (Objects.equals(deck.getOwner(), id)) {
            throw new ConflictException();
        }
        userRepository.findUserByTgId(id).orElseThrow(NotFoundException::new);
        userRepository.removeDeckFromUser(id, deckId);
    }

    public void shareDeckWithUser(Long id, Integer deckId) {
        userRepository.findUserByTgId(id).orElseThrow(NotFoundException::new);
        deckRepository.findDeckById(deckId).orElseThrow(NotFoundException::new);
        try {
            userRepository.shareDeckWithUser(id, deckId);
        } catch (DataAccessException exception) {
            throw new ConflictException();
        }
    }
}
