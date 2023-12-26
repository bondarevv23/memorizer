package memorizer.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.RegexCommandHandler
import eu.vendeli.tgbot.api.*
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.ProcessedUpdate
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardButton
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardMarkup
import memorizer.botController.ControllerUtilities.deleteMessages
import org.springframework.stereotype.Component

@Component
class MenuController {

    companion object {
        private val MENU_KEYBOARD = listOf(
            listOf(InlineKeyboardButton(text = "\uD83D\uDCDC  my decks", callbackData = "decks")),
            listOf(InlineKeyboardButton(text = "➕  new deck", callbackData = "new_deck")),
            listOf(
                InlineKeyboardButton(text = "❔ help", callbackData = "help"),
                InlineKeyboardButton(
                    text = "\uD83C\uDDEC\uD83C\uDDE7 / \uD83C\uDDF7\uD83C\uDDFA",
                    callbackData = "change_language"
                )
            )
        )
        private const val MENU_TEXT = "menu information";
    }

    @CommandHandler(["/menu", "/start"])
    suspend fun menu(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        if (update is CallbackQueryUpdate) {
            editMessageText(update.callbackQuery.message?.messageId!!){
                MENU_TEXT
            }.options { parseMode = ParseMode.Markdown }.markup {
                InlineKeyboardMarkup(MENU_KEYBOARD)
            }.send(user, bot)
            answerCallbackQuery(update.callbackQuery.id).send(user, bot)
        } else {
            sendMessage{
                MENU_TEXT
            }.options { parseMode = ParseMode.Markdown }.markup{
                InlineKeyboardMarkup(MENU_KEYBOARD)
            }.send(user, bot)
            deleteMessage(update.update.message?.messageId!!).send(user, bot)
        }
    }
}
