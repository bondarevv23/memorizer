package memorizer.config

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.botactions.SetMyCommandsAction
import eu.vendeli.tgbot.api.botactions.getMyCommands
import eu.vendeli.tgbot.api.botactions.setMyCommands
import eu.vendeli.tgbot.api.botactions.setWebhook
import eu.vendeli.tgbot.api.chat.setChatMenuButton
import eu.vendeli.tgbot.types.bot.BotCommand
import eu.vendeli.tgbot.types.bot.BotCommandScope
import eu.vendeli.tgbot.types.keyboard.MenuButton
import memorizer.implementation.SpringClassManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TelegramBotConfig (
    @Value("\${bot-token}")
    private val botToken: String,
    private val springClassManager: SpringClassManager
) {
    @Bean
    open suspend fun instance(): TelegramBot {
        val bot = TelegramBot(botToken, "memorizer.botController") {
            classManager = springClassManager
        }

        bot.update.setBehaviour {
            handle(it)
        }

        return bot
    }
}
