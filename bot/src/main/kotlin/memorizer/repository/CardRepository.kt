package memorizer.repository

import memorizer.model.redis.Card

interface CardRepository {
    public fun save (card: Card) : Card

    public fun findById(id: Long): Card?

    public fun deleteById(id: Long)
}
