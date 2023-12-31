package memorizer.webhook

import eu.vendeli.tgbot.TelegramBot
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BotWebhookController(
    private val telegramBot: TelegramBot
) {
    @PostMapping("/webhook")
    suspend fun webhook(@RequestBody update: String) {
        telegramBot.update.parseAndHandle(update)
    }
}
