package memorizer.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.types.Message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import kotlinx.coroutines.runBlocking
import reactor.core.publisher.Mono

object ControllerUtilities {
    @JvmStatic
    public fun <T> subscribeBlocking(
        mono: Mono<T>,
        success: suspend (T) -> Unit,
        failure: suspend (Throwable) -> Unit = { }
    ) {
        mono.subscribe(
            {
                runBlocking { success(it) }
            },
            {
                runBlocking { failure(it) }
            }
        )
    }

    public suspend fun deleteMessages(user: User, bot: TelegramBot, message: Message, count: Int = 1) {
        val messageId = message.messageId
        for (i in messageId - count + 1..messageId) {
            deleteMessage(i).send(user, bot)
        }
    }
}
