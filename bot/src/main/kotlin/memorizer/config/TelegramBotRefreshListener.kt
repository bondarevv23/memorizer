package memorizer.config

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.botactions.setMyCommands
import kotlinx.coroutines.runBlocking

@Component
class TelegramBotRefreshListener(
    private val bot: TelegramBot
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        runBlocking {
            setMyCommands {
                botCommand("/menu", "menu")
            }.send(bot)
            bot.handleUpdates()
        }
    }
}
