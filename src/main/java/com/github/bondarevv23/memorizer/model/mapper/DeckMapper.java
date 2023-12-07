package com.github.bondarevv23.memorizer.model.mapper;

import com.github.bondarevv23.memorizer.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface DeckMapper {
    DeckDTO deckToDeckDTO(Deck deck);

    Deck deckRequestToDeck(DeckRequest request);

    List<DeckDTO> listDeckToListDeckDTO(List<Deck> decks);
}
