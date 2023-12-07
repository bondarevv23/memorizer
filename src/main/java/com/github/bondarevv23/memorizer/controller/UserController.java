package com.github.bondarevv23.memorizer.controller;

import com.github.bondarevv23.memorizer.api.UsersApi;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.DeckDTO;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.model.UserDTO;
import com.github.bondarevv23.memorizer.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.model.mapper.UserMapper;
import com.github.bondarevv23.memorizer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {
    private final UserMapper userMapper;
    private final DeckMapper deckMapper;
    private final UserService service;

    @Override
    public ResponseEntity<UserDTO> addNewUser(Long id) {
        User user = service.addNewUser(id);
        UserDTO userDTO = userMapper.userToUserDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long id) {
        service.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<DeckDTO>> getDecksByUserId(Long id) {
        List<Deck> decks = service.getDecksByUserId(id);
        List<DeckDTO> deckDTOs = deckMapper.listDeckToListDeckDTO(decks);
        return ResponseEntity.ok(deckDTOs);
    }

    @Override
    public ResponseEntity<Void> removeDeckFromUser(Long id, Integer deckId) {
        service.removeDeckFromUser(id, deckId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> shareDeckWithUser(Long id, Integer deckId) {
        service.shareDeckWithUser(id, deckId);
        return ResponseEntity.ok().build();
    }
}
