package memorizer.repository

import memorizer.model.redis.Deck

interface DeckRepository {
    public fun save (deck: Deck) : Deck

    public fun findById(id: Long): Deck?
}