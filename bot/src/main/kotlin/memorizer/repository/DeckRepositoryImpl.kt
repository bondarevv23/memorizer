package memorizer.repository

import memorizer.model.redis.Deck
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component


@Component
open class DeckRepositoryImpl (
    private val template: RedisTemplate<Long, Deck>
) : DeckRepository {
    public override fun save (deck: Deck) : Deck {
        template.opsForValue().set(deck.userId, deck)
        return deck
    }

    public override fun findById(id: Long): Deck? {
        return template.opsForValue().get(id)
    }
}
