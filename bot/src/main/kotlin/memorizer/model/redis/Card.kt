package memorizer.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Card")
data class Card (
    @Id
    val userId: Long,
    val deckId: Int,
    val question: Side,
    val answer: Side,
    val id: Int? = null
) : Serializable {
    public constructor(userId: Long, deckId: Int) : this(userId, deckId, Side(), Side())
}
