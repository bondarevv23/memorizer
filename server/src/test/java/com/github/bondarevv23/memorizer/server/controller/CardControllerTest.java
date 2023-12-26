package com.github.bondarevv23.memorizer.server.controller;

import com.github.bondarevv23.memorizer.server.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.server.model.*;
import com.github.bondarevv23.memorizer.server.model.generated.CardDTO;
import com.github.bondarevv23.memorizer.server.model.generated.CardRequest;
import com.github.bondarevv23.memorizer.server.model.generated.SideDTO;
import com.github.bondarevv23.memorizer.server.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.server.repository.interfaces.CardRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.DeckRepository;
import com.github.bondarevv23.memorizer.server.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.server.service.CardService;
import com.github.bondarevv23.memorizer.server.util.TestHttpRequestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.bondarevv23.memorizer.server.util.TestUtil.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CardControllerTest extends IntegrationEnvironment {
    @LocalServerPort
    private int port;

    private TestHttpRequestUtil client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private CardMapper cardMapper;

    private User user;
    private Deck deck;
    private Deck wrongDeck;

    @BeforeAll
    public void initDeck() {
        user = userRepository.save(User.builder().tgId(1L).build());
        deck = deckRepository.save(Deck.builder()
                .title("deck")
                .owner(user.getTgId())
                .build());
        wrongDeck = Deck.builder().id(-1).owner(-1L).title("deck").build();
    }

    @AfterAll
    public void deleteDeck() {
        deckRepository.deleteDeckById(deck.getId());
        userRepository.deleteUserByTgId(user.getTgId());
    }

    @BeforeAll
    public void initWebClient() {
        client = new TestHttpRequestUtil(WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build());
    }

    @AfterEach
    public void cleanData() {
        cardRepository.findCardsByDeckId(deck.getId()).forEach(
                card -> cardRepository.deleteCardById(card.getId())
        );
    }

    @Test
    void whenAddNewCardWithRightRequest_thenOKAndRightCardDTO() {
        // given
        int i = 1;
        CardRequest request = getCardRequest(deck, i);

        // when
        ResponseEntity<CardDTO> response = client.makePost(request, CardDTO.class, "/cards");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CardDTO cardDTO = response.getBody();
        CardDTO expected = getCardDTO(deck, i);
        assertThat(cardDTO).isEqualToIgnoringGivenFields(expected, "id");
    }

    @Test
    void whenAddNewCardByWrongRequest_thenBadRequest() {
        // given
        int i = 1;
        SideDTO wrongRequest = getSideDto("wrong", i);

        // when
        ResponseEntity<CardDTO> response = client.makePost(wrongRequest, CardDTO.class, "/cards");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenAddNewCardWithWrongDeckId_thenNotFound() {
        // given
        int i = 1;
        CardRequest wrongRequest = getCardRequest(wrongDeck, i);

        // when
        ResponseEntity<CardDTO> response = client.makePost(wrongRequest, CardDTO.class, "/cards");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenDeleteCardByRightId_thenOK() {
        // given
        int i = 1;
        Card card = cardService.addNewCard(getCardRequest(deck, i));

        // when
        ResponseEntity<Void> response = client.makeDelete("/cards/{id}", card.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenDeleteCardByWrongId_thenNotFound() {
        // given
        int wrongCardId = -1;

        // when
        ResponseEntity<Void> response = client.makeDelete("/cards/{id}", wrongCardId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetCardByRightId_thenOKAndRightCardDTO() {
        // given
        int i = 1;
        Card card = cardService.addNewCard(getCardRequest(deck, i));

        // when
        ResponseEntity<CardDTO> response = client.makeGet(CardDTO.class, "/cards/{id}", card.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CardDTO assertedDTO = cardMapper.cardToCardDTO(card);
        assertThat(response.getBody()).isEqualTo(assertedDTO);
    }

    @Test
    void whenGetCardByWrongId_thenNotFound() {
        // given
        int wrongId = -1;

        // when
        ResponseEntity<CardDTO> response = client.makeGet(CardDTO.class, "/cards/{id}", wrongId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenUpdateCardByIdWithRightRequest_thenOKAndCardHasUpdated() {
        // given
        Card card = cardService.addNewCard(getCardRequest(deck, 1));
        CardRequest updateRequest = getCardRequest(deck, 2);

        // when
        ResponseEntity<Void> response = client.makePut(updateRequest,"/cards/{id}", card.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Card storedCard = cardService.getCardById(card.getId());
        CardDTO expectedDTO = cardMapper.cardToCardDTO(storedCard);
        CardDTO assertedDTO = cardMapper.cardToCardDTO(cardMapper.cardRequestToCard(updateRequest));
        assertThat(assertedDTO).isEqualToIgnoringGivenFields(expectedDTO, "id");
    }

    @Test
    void whenUpdateCardByWrongId_thenNotFound() {
        // given
        int wrongId = -1;
        CardRequest wrongIdRequest = getCardRequest(deck, 2);

        // when
        ResponseEntity<Void> wrongIdResponse = client.makePut(wrongIdRequest,"/cards/{id}", wrongId);

        // then
        assertThat(wrongIdResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenUpdateCardByWrongRequest_thenBadRequest() {
        // given
        int wrongId = -1;
        SideDTO wrongRequest = getSideDto("wrong", 1);

        // when
        ResponseEntity<Void> wrongIdResponse = client.makePut(wrongRequest,"/cards/{id}", wrongId);

        // then
        assertThat(wrongIdResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
