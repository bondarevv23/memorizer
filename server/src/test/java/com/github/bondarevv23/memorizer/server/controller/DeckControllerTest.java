package com.github.bondarevv23.memorizer.server.controller;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.*;
import com.github.bondarevv23.memorizer.server.model.generated.CardDTO;
import com.github.bondarevv23.memorizer.server.model.generated.DeckDTO;
import com.github.bondarevv23.memorizer.server.model.generated.DeckRequest;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.server.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.service.CardService;
import com.github.bondarevv23.memorizer.server.service.DeckService;
import com.github.bondarevv23.memorizer.server.service.UserService;
import com.github.bondarevv23.memorizer.server.util.TestHttpRequestUtil;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.IntStream;

import static com.github.bondarevv23.memorizer.server.util.TestUtil.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeckControllerTest extends IntegrationEnvironment {
    @LocalServerPort
    private int port;

    private TestHttpRequestUtil client;

    @BeforeAll
    public void initClient() {
        client = new TestHttpRequestUtil(WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build());
    }

    @Autowired
    private UserService userService;

    private User user;

    @BeforeAll
    public void initUser() {
        user = userService.addNewUser(1L);
    }

    @AfterAll
    public void deleteUser() {
        userService.deleteUserById(user.getTgId());
    }

    @Autowired
    private DeckService deckService;

    @Autowired
    private DeckRepository deckRepository;

    @AfterEach
    public void cleanData() {
        deckRepository.findAll().stream().map(Deck::getId).forEach(deckService::deleteDeckById);
    }

    private final DeckMapper deckMapper = Mappers.getMapper(DeckMapper.class);
    private final CardMapper carsMapper = Mappers.getMapper(CardMapper.class);

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Test
    void whenAddNewDeckWithRightDeckRequest_thenOKRightDTOReturned() {
        // given
        DeckRequest request = getDeckRequest(user, 1);

        // when
        ResponseEntity<DeckDTO> response = client.makePost(request, DeckDTO.class, "/decks");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DeckDTO expectedDTO = deckMapper.deckToDeckDTO(deckMapper.deckRequestToDeck(request));
        assertThat(expectedDTO).isEqualToIgnoringGivenFields(request, "id");
    }

    @Test
    void whenAddNewDeckWithWrongRequest_thenBadRequest() {
        // given
        Integer wrongRequest = 1;

        // when
        ResponseEntity<DeckDTO> response = client.makePost(wrongRequest, DeckDTO.class, "/decks");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenAddNewDeckWithWrongOwnerId_thenNotFoundException() {
        // given
        DeckRequest requestWithWrongUserId = getDeckRequest(User.builder().tgId(-1L).build(), 1);

        // when
        ResponseEntity<DeckDTO> response = client.makePost(requestWithWrongUserId, DeckDTO.class, "/decks");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenDeleteDeckByRightId_thenOKAndAllCardsFromThisDeckHaveDeleted() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        IntStream.range(0, 5).mapToObj(i -> getCardRequest(deck, i)).forEach(cardService::addNewCard);

        // when
        ResponseEntity<Void> response = client.makeDelete("/decks/{id}", deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cardRepository.findCardsByDeckId(deck.getId())).isEmpty();
    }

    @Test
    void whenDeleteDeckByWrongId_thenNotFound() {
        // given
        int wrongDeckId = -1;

        // when
        ResponseEntity<Void> response = client.makeDelete("/decks/{id}", wrongDeckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetDeckByRightId_thenOKAndDeckHasReturned() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        DeckDTO expectedDTO = deckMapper.deckToDeckDTO(deck);

        // when
        ResponseEntity<DeckDTO> response = client.makeGet(DeckDTO.class, "/decks/{id}", deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDTO);
    }

    @Test
    void whenGetDeckByWrongId_thenNotFound() {
        // given
        int wrongDeckId = -1;

        // when
        ResponseEntity<DeckDTO> response = client.makeGet(DeckDTO.class, "/decks/{id}", wrongDeckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetRandomCardFromDeck_thenOKAndSomeCardHasReturned() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        Card card = cardService.addNewCard(getCardRequest(deck, 1));
        CardDTO expectedDTO = carsMapper.cardToCardDTO(card);

        // when
        ResponseEntity<CardDTO> response = client.makeGet(CardDTO.class, "/decks/{id}/card", deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDTO);
    }

    @Test
    void whenGetRandomCardFromDeckByWrongId_thenNotFound() {
        // given
        int wrongDeckId = -1;

        // when
        ResponseEntity<DeckDTO> response = client.makeGet(DeckDTO.class, "/decks/{id}/card", wrongDeckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetRandomCardFromEmptyDeck_thenConflict() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));

        // when
        ResponseEntity<DeckDTO> response = client.makeGet(DeckDTO.class, "/decks/{id}/card", deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenUpdateDeckById_thenOKAndDeckHasUpdated() {
        // given
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        DeckRequest request = getDeckRequest(user, 2);

        // when
        ResponseEntity<Void> response = client.makePut(request, "/decks/{id}", deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Deck expectedDeck = deckMapper.deckRequestToDeck(request);
        assertThat(deckService.getDeckById(deck.getId()))
                .isEqualToIgnoringGivenFields(expectedDeck, "id");
    }

    @Test
    void whenUpdateDeckByWrongId_thenNotFoundException() {
        // given
        DeckRequest request = getDeckRequest(user, 1);
        int wrongDeckId = -1;

        // when
        ResponseEntity<Void> response = client.makePut(request, "/decks/{id}", wrongDeckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenUpdateDeckByWrongRequest_thenBadRequest() {
        // given
        Integer wrongRequest =1;
        int someDeckId = 1;

        // when
        ResponseEntity<Void> response = client.makePut(wrongRequest, "/decks/{id}", someDeckId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
