package memorizer.client

import memorizer.model.redis.Card
import memorizer.model.rest.CardResponse
import memorizer.model.rest.DeckRequest
import memorizer.model.rest.DeckResponse
import memorizer.model.rest.UserResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono


@Component
class MemorizerClientImpl (
    private val memorizerWebClient: WebClient
) : MemorizerClient {
    override fun register(userId: Long) : Mono<UserResponse> =
        memorizerWebClient.post()
            .uri("/users/$userId")
            .retrieve()
            .bodyToMono<UserResponse>()

    override fun createNewDeck(userId: Long, title: String): Mono<DeckResponse> =
        memorizerWebClient.post()
            .uri("/decks")
            .bodyValue(DeckRequest(userId, title))
            .retrieve()
            .bodyToMono<DeckResponse>()

    override fun getAllDecks(userId: Long): Mono<List<DeckResponse>> =
        memorizerWebClient.get()
            .uri("/users/$userId")
            .retrieve()
            .bodyToMono<List<DeckResponse>>()

    override fun getCardsByDeckId(deckId: Int): Mono<List<CardResponse>> =
        memorizerWebClient.get()
            .uri("/decks/$deckId/cards")
            .retrieve()
            .bodyToMono<List<CardResponse>>()

    override fun getDeckById(deckId: Int): Mono<DeckResponse> =
        memorizerWebClient.get()
            .uri("/decks/$deckId")
            .retrieve()
            .bodyToMono<DeckResponse>()

    override fun deleteDeckById(deckId: Int): Mono<Boolean> =
        memorizerWebClient.delete()
            .uri("/decks/$deckId")
            .retrieve().toEntity(Unit.javaClass).map { it.statusCode.is2xxSuccessful }

    override fun updateDeckById(deckId: Int, deckRequest: DeckRequest): Mono<Boolean> =
        memorizerWebClient.put()
            .uri("/decks/$deckId")
            .bodyValue(deckRequest)
            .retrieve().toEntity(Unit.javaClass).map { it.statusCode.is2xxSuccessful }

    override fun addNewCard(card: Card): Mono<CardResponse> =
        memorizerWebClient.post()
            .uri("/cards")
            .bodyValue(card)
            .retrieve()
            .bodyToMono<CardResponse>()

    override fun getCardById(cardId: Int): Mono<CardResponse> =
        memorizerWebClient.get()
            .uri("/cards/$cardId")
            .retrieve()
            .bodyToMono<CardResponse>()

    override fun getRandomCardByDeckId(deckId: Int): Mono<CardResponse> =
        memorizerWebClient.get()
            .uri("/decks/$deckId/card")
            .retrieve()
            .bodyToMono<CardResponse>()

    override fun updateCardById(cardId: Int, card: Card): Mono<Boolean> =
        memorizerWebClient.put()
            .uri("/cards/$cardId")
            .bodyValue(card)
            .retrieve()
            .toEntity(Unit.javaClass).map { it.statusCode.is2xxSuccessful }

    override fun deleteCardById(cardId: Int): Mono<Boolean> =
        memorizerWebClient.delete()
            .uri("/cards/$cardId")
            .retrieve()
            .toEntity(Unit.javaClass).map { it.statusCode.is2xxSuccessful }
}
