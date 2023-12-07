package com.github.bondarevv23.memorizer.controller;

import com.github.bondarevv23.memorizer.api.CardsApi;
import com.github.bondarevv23.memorizer.model.Card;
import com.github.bondarevv23.memorizer.model.CardDTO;
import com.github.bondarevv23.memorizer.model.CardRequest;
import com.github.bondarevv23.memorizer.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardController implements CardsApi {
    private final CardService service;
    private final CardMapper mapper;

    @Override
    public ResponseEntity<CardDTO> addNewCard(CardRequest cardRequest) {
        Card card = service.addNewCard(cardRequest);
        CardDTO cardDTO = mapper.cardToCardDTO(card);
        return ResponseEntity.ok(cardDTO);
    }

    @Override
    public ResponseEntity<Void> deleteCardById(Integer id) {
        service.deleteCardById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CardDTO> getCardById(Integer id) {
        Card card = service.getCardById(id);
        CardDTO cardDTO = mapper.cardToCardDTO(card);
        return ResponseEntity.ok(cardDTO);
    }

    @Override
    public ResponseEntity<Void> updateCardById(Integer id, CardRequest cardRequest) {
        service.updateCardById(id, cardRequest);
        return ResponseEntity.ok().build();
    }
}
