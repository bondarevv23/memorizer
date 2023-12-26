package com.github.bondarevv23.memorizer.server.model.mapper;

import com.github.bondarevv23.memorizer.server.model.generated.*;
import com.github.bondarevv23.memorizer.server.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeckMapper {
    DeckDTO deckToDeckDTO(Deck deck);

    Deck deckRequestToDeck(DeckRequest request);

    List<DeckDTO> listDeckToListDeckDTO(List<Deck> decks);
}
