package com.github.bondarevv23.memorizer.model.mapper;

import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;


public interface UserMapper {
    @Mapping(source = "decks", target = "deckIds")
    UserDTO userToUserDTO(User user);

    default List<Integer> getDeckIds(List<Deck> decks) {
        return decks.stream().map(Deck::getId).collect(Collectors.toList());
    }
}
