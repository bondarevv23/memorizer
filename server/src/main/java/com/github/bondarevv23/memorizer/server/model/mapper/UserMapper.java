package com.github.bondarevv23.memorizer.server.model.mapper;

import com.github.bondarevv23.memorizer.server.model.generated.*;
import com.github.bondarevv23.memorizer.server.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "decks", target = "deckIds")
    UserDTO userToUserDTO(User user);

    default List<Integer> getDeckIds(List<Deck> decks) {
        return decks.stream().map(Deck::getId).collect(Collectors.toList());
    }
}
