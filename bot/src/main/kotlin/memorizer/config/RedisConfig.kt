package memorizer.config

import memorizer.model.redis.Card
import memorizer.model.redis.Deck
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
open class RedisConfig {

    @Bean
    open fun lettuceConnectionFactory() : LettuceConnectionFactory {
        val factory = LettuceConnectionFactory("localhost", 6379)
        factory.password = "redis-password"
        return factory
    }

    @Bean
    open fun deckRedisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<Long, Deck> {
        val template = RedisTemplate<Long, Deck>()
        template.connectionFactory = connectionFactory
        return template
    }

    @Bean
    open fun cardRedisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<Long, Card> {
        val template = RedisTemplate<Long, Card>()
        template.connectionFactory = connectionFactory
        return template
    }
}
