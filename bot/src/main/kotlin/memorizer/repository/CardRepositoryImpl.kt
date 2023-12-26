package memorizer.repository

import memorizer.model.redis.Card
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class CardRepositoryImpl (
    private val template: RedisTemplate<Long, Card>
) : CardRepository {
    public override fun save (card: Card) : Card {
        template.opsForValue().set(card.userId, card)
        return card
    }

    public override fun findById(id: Long): Card? {
        return template.opsForValue().get(id)
    }

    public override fun deleteById(id: Long) {
        template.delete(id)
    }
}
