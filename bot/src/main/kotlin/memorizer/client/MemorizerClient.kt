package memorizer.client

import memorizer.model.redis.Card
import memorizer.model.rest.*
import reactor.core.publisher.Mono


interface MemorizerClient {
    fun register(userId: Long) : Mono<UserResponse>

    fun createNewDeck(userId: Long, title: String) : Mono<DeckResponse>

    fun getAllDecks(userId: Long) : Mono<List<DeckResponse>>

    fun getCardsByDeckId(deckId: Int) : Mono<List<CardResponse>>

    fun getDeckById(deckId: Int) : Mono<DeckResponse>

    fun deleteDeckById(deckId: Int) : Mono<Boolean>

    fun updateDeckById(deckId: Int, deckRequest: DeckRequest) : Mono<Boolean>

    fun addNewCard(card: Card) : Mono<CardResponse>

    fun getCardById(cardId: Int) : Mono<CardResponse>

    fun getRandomCardByDeckId(deckId: Int) : Mono<CardResponse>

    fun updateCardById(cardId: Int, card: Card) : Mono<Boolean>

    fun deleteCardById(cardId: Int) : Mono<Boolean>
}
