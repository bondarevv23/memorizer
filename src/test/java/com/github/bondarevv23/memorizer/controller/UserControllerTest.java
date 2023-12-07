package com.github.bondarevv23.memorizer.controller;

import com.github.bondarevv23.memorizer.IntegrationEnvironment;
import com.github.bondarevv23.memorizer.model.Deck;
import com.github.bondarevv23.memorizer.model.DeckDTO;
import com.github.bondarevv23.memorizer.model.User;
import com.github.bondarevv23.memorizer.model.UserDTO;
import com.github.bondarevv23.memorizer.model.mapper.CardMapper;
import com.github.bondarevv23.memorizer.model.mapper.DeckMapper;
import com.github.bondarevv23.memorizer.repository.interfaces.UserRepository;
import com.github.bondarevv23.memorizer.service.DeckService;
import com.github.bondarevv23.memorizer.service.UserService;
import com.github.bondarevv23.memorizer.util.TestHttpRequestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.bondarevv23.memorizer.util.TestUtil.getDeckRequest;
import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends IntegrationEnvironment {
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
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DeckService deckService;

    private final DeckMapper deckMapper = Mappers.getMapper(DeckMapper.class);

    @AfterEach
    public void cleanData() {
        userRepository.findAll().stream().map(User::getTgId).forEach(userService::deleteUserById);
    }

    @Test
    void whenAddNewUser_thenOK() {
        // given
        long userId = 1L;

        // when
        ResponseEntity<UserDTO> response = client.makePost(UserDTO.class, "/users/{id}", userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTgId()).isEqualTo(userId);
    }

    @Test
    void whenAddNewUserWithAlreadyExistsId_thenConflict() {
        // given
        long userId = 1L;
        userService.addNewUser(userId);

        // when
        ResponseEntity<UserDTO> response = client.makePost(UserDTO.class, "/users/{id}", userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenDeleteUserByRightId_thenOK() {
        // given
        User user = userService.addNewUser(1L);

        // when
        ResponseEntity<Void> response = client.makeDelete("/users/{id}", user.getTgId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenDeleteUserByWrongId_thenNotFound() {
        // given
        long wrongUserId = -1;

        // when
        ResponseEntity<Void> response = client.makeDelete("/users/{id}", wrongUserId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetDecksByRightUserId_thenOKAndAllUserDecksHaveReturned() {
        // given
        User user = userService.addNewUser(1L);
        List<DeckDTO> expectedDTOs = IntStream.range(0, 5)
                .mapToObj(i -> getDeckRequest(user, i))
                .map(deckService::addNewDeck)
                .map(deckMapper::deckToDeckDTO)
                .toList();

        // when
        ResponseEntity<List<DeckDTO>> response = client.makeGetList(DeckDTO.class, "/users/{id}", user.getTgId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsOnlyElementsOf(expectedDTOs);
    }

    @Test
    void whenGetDecksByWrongUserId_thenNotFound() {
        // given
        long wrongUserId = -1L;

        // when
        ResponseEntity<List<DeckDTO>> response = client.makeGetList(DeckDTO.class, "/users/{id}", wrongUserId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenRemoveDeckFromUserById_thenOK() {
        // given
        User user1 = userService.addNewUser(1L);
        User user2 = userService.addNewUser(2L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user1, 1));
        userService.shareDeckWithUser(user2.getTgId(), deck.getId());

        // when
        ResponseEntity<Void> response = client.makeDelete(
                "/users/{id}/remove/{deckId}",
                user2.getTgId(),
                deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenRemoveDeckFromOwnerById_thenConflict() {
        // given
        User user = userService.addNewUser(1L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));

        // when
        ResponseEntity<Void> response = client.makeDelete(
                "/users/{id}/remove/{deckId}",
                user.getTgId(),
                deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenRemoveDeckFromOwnerByWrongId_thenNotFound() {
        // given
        User user = userService.addNewUser(1L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        long wrongUserId = -1L;
        int wrongDeckId = -1;

        // when
        ResponseEntity<Void> response1 = client.makeDelete(
                "/users/{id}/remove/{deckId}",
                user.getTgId(),
                wrongDeckId);
        ResponseEntity<Void> response2 = client.makeDelete(
                "/users/{id}/remove/{deckId}",
                wrongUserId,
                deck.getId());
        ResponseEntity<Void> response3 = client.makeDelete(
                "/users/{id}/remove/{deckId}",
                wrongUserId,
                wrongDeckId);

        // then
        Stream.of(response1, response2, response3).forEach(
                res -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }

    @Test
    void whenShareDeckWithUser_thenOK() {
        // given
        User user1 = userService.addNewUser(1L);
        User user2 = userService.addNewUser(2L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user1, 1));

        // when
        ResponseEntity<Void> response = client.makePut(
                Void.class,
                "/users/{id}/share/{deckId}",
                user2.getTgId(),
                deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenShareDeckWithOwnerByWrongId_thenNotFound() {
        // given
        User user = userService.addNewUser(1L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));
        long wrongUserId = -1L;
        int wrongDeckId = -1;

        // when
        ResponseEntity<Void> response1 = client.makePut(
                Void.class,
                "/users/{id}/share/{deckId}",
                user.getTgId(),
                wrongDeckId);
        ResponseEntity<Void> response2 = client.makePut(
                Void.class,
                "/users/{id}/share/{deckId}",
                wrongUserId,
                deck.getId());
        ResponseEntity<Void> response3 = client.makePut(
                Void.class,
                "/users/{id}/share/{deckId}",
                wrongUserId,
                wrongDeckId);

        // then
        Stream.of(response1, response2, response3).forEach(
                res -> assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }

    @Test
    void whenShareDeckWithUserThatAlreadyHasIt_thenConflict() {
        // given
        User user = userService.addNewUser(1L);
        Deck deck = deckService.addNewDeck(getDeckRequest(user, 1));

        // when
        ResponseEntity<Void> response = client.makePut(
                Void.class,
                "/users/{id}/share/{deckId}",
                user.getTgId(),
                deck.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
