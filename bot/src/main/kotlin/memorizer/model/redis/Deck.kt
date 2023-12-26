package memorizer.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Deck")
data class Deck (
    @Id
    val userId: Long,
    val deckId: Int
) : Serializable
