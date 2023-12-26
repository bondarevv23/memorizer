package com.github.bondarevv23.memorizer.server.controller;

import com.github.bondarevv23.memorizer.server.controller.api.DecksApi;
import com.github.bondarevv23.memorizer.server.model.*;
import com.github.bondarevv23.memorizer.server.model.generated.CardDTO;
import com.github.bondarevv23.memorizer.server.model.generated.DeckDTO;
import com.github.bondarevv23.memorizer.server.model.generated.DeckRequest;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.server.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.server.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeckController implements DecksApi {
    private final DeckMapper deckMapper;
    private final CardMapper cardMapper;
    private final DeckService service;

    @Override
    public ResponseEntity<DeckDTO> addNewDeck(DeckRequest deckRequest) {
        Deck deck = service.addNewDeck(deckRequest);
        DeckDTO deckDTO = deckMapper.deckToDeckDTO(deck);
        return ResponseEntity.ok(deckDTO);
    }

    @Override
    public ResponseEntity<Void> deleteDeckById(Integer id) {
        service.deleteDeckById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CardDTO>> getCardsFromDeck(Integer id) {
        return ResponseEntity.ok(service.getAllCardsFromDeck(id).stream().map(cardMapper::cardToCardDTO).toList());
    }

    @Override
    public ResponseEntity<DeckDTO> getDeckById(Integer id) {
        Deck deck = service.getDeckById(id);
        DeckDTO deckDTO = deckMapper.deckToDeckDTO(deck);
        return ResponseEntity.ok(deckDTO);
    }

    @Override
    public ResponseEntity<CardDTO> getRandomCardFromDeck(Integer id) {
        Card card = service.getRandomCardFromDeck(id);
        CardDTO deckDTO = cardMapper.cardToCardDTO(card);
        return ResponseEntity.ok(deckDTO);
    }

    @Override
    public ResponseEntity<Void> updateDeckById(Integer id, DeckRequest deckRequest) {
        service.updateDeckById(id, deckRequest);
        return ResponseEntity.ok().build();
    }
}
