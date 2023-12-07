package com.github.bondarevv23.memorizer.model.mapper;

import com.github.bondarevv23.memorizer.model.Card;
import com.github.bondarevv23.memorizer.model.CardDTO;
import com.github.bondarevv23.memorizer.model.CardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public interface CardMapper {
    Card cardRequestToCard(CardRequest request);

    CardDTO cardToCardDTO(Card card);
}
