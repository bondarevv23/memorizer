package com.github.bondarevv23.memorizer.server.model.mapper;

import com.github.bondarevv23.memorizer.server.model.generated.*;
import com.github.bondarevv23.memorizer.server.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    Card cardRequestToCard(CardRequest request);

    @Mapping(target = "deckId", expression = "java(card.getDeck().getId())")
    CardDTO cardToCardDTO(Card card);
}
