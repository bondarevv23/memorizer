package memorizer.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.RegexCommandHandler
import eu.vendeli.tgbot.api.*
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.CommandScope
import eu.vendeli.tgbot.types.internal.MessageUpdate
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardButton
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardMarkup
import memorizer.botController.ControllerUtilities.deleteMessages
import memorizer.botController.ControllerUtilities.subscribeBlocking
import memorizer.client.MemorizerClient
import memorizer.model.redis.Deck
import memorizer.model.rest.DeckRequest
import memorizer.model.rest.DeckResponse
import memorizer.repository.DeckRepository
import memorizer.repository.DeckRepositoryImpl
import org.springframework.stereotype.Component

@Component
class DeckController (
    private val client: MemorizerClient,
    private val repository: DeckRepository
) {

    @CommandHandler(value = ["decks"], scope = [CommandScope.CALLBACK])
    suspend fun listOfDecks(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        subscribeBlocking(client.getAllDecks(user.id), {
            if (it.isEmpty()) {
                editMessageText(update.callbackQuery.message?.messageId!!) {
                    "there is no any decks yet :("
                }.inlineKeyboardMarkup {
                    "create new deck" callback "new_deck"
                }.send(user, bot)
                return@subscribeBlocking
            }
            val keyboard = it.mapIndexed { i: Int, res: DeckResponse ->
                listOf(InlineKeyboardButton(text = "${i + 1}.  ${res.title}", callbackData = "deck_${res.id}"))
            }
            keyboard.addLast(listOf(InlineKeyboardButton(text = "⬅\uFE0F  menu", callbackData = "/menu")))
            editMessageText(update.callbackQuery.message?.messageId!!){
                "all available decks"
            }.markup{
                InlineKeyboardMarkup(keyboard)
            }.send(user, bot)
        })
        answerCallbackQuery(update.callbackQuery.id).send(user, bot)
    }

    @CommandHandler(value = ["new_deck"], scope = [CommandScope.CALLBACK])
    suspend fun createDeck(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        editMessageText(update.callbackQuery.message?.messageId!!) {
            "write a title of new deck"
        }.inlineKeyboardMarkup {
            "back to menu" callback "/menu"
        }.send(user, bot)
        bot.inputListener[user] = "getDeckTitleCreate"
        answerCallbackQuery(update.callbackQuery.id).send(user.id, bot)
    }

    @InputHandler(["getDeckTitleCreate"])
    suspend fun getDeckTitleCreate(update: MessageUpdate, user: User, bot: TelegramBot) {
        if (update.text.isNotEmpty()) {
            subscribeBlocking(client.createNewDeck(user.id, update.text), {
                message {
                    "deck with title '${it.title}' created!"
                }.inlineKeyboardMarkup {
                    "menu" callback "/menu"
                }.send(user.id, bot)
                deleteMessages(user, bot, update.message, 2)
            })
            return
        }
        message {
            "invalid type for title :("
        }.inlineKeyboardMarkup {
            "menu" callback "/menu"
        }.send(user.id, bot)
        deleteMessages(user, bot, update.message, 2)
    }

    @RegexCommandHandler(value = "^deck_[0-9]+")
    suspend fun getDeck(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = update.text.substring(5).toInt()
        subscribeBlocking(client.getDeckById(deckId), {
            sendMessage {
                "*${it.title}*"
            }.options { parseMode = ParseMode.Markdown }.inlineKeyboardMarkup {
                "\uD83D\uDCDC  list of cards" callback "cards_$deckId"
                newLine()
                "\uD83C\uDFB2  get random card" callback "random_card_$deckId"
                newLine()
                "\uD83D\uDDD1  delete deck" callback "delete_deck_$deckId"
                "➕  add new card" callback "add_card_$deckId"
                newLine()
                "⬅\uFE0F  menu" callback "/menu"
                "✏\uFE0F  edit deck" callback "edit_deck_$deckId"
            }.send(user, bot)
            deleteMessage(update.callbackQuery.message!!.messageId).send(user.id, bot)
        })
    }

    @RegexCommandHandler(value = "^cards_[0-9]+")
    suspend fun getCards(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = update.text.substring(6).toInt()
        subscribeBlocking(client.getCardsByDeckId(deckId), {
            if (it.isEmpty()) {
                editMessageText(update.callbackQuery.message?.messageId!!) {
                    "this deck is empty :("
                }.inlineKeyboardMarkup {
                    "⬅\uFE0F  back to deck" callback "deck_$deckId"
                    "➕  add new card" callback "add_card_$deckId"
                }.send(user, bot)
                return@subscribeBlocking
            }
            val buttons = mutableListOf(listOf(
                InlineKeyboardButton(text = "⬅\uFE0F  back to deck", callbackData = "deck_$deckId"),
                InlineKeyboardButton(text = "➕  add new card", callbackData = "add_card_$deckId"),
            ))
            it.forEachIndexed { index, card ->
                buttons.addLast(listOf(InlineKeyboardButton(
                    text = "${index + 1}. ${card.question.title}",
                    callbackData = "card_${card.id}"
                )))
            }
            editMessageReplyMarkup(update.callbackQuery.message!!.messageId)
                .markup(InlineKeyboardMarkup(buttons))
                .send(user, bot)
            answerCallbackQuery(update.callbackQuery.id).send(user.id, bot)
        })
    }

    @RegexCommandHandler(value = "^delete_deck_[0-9]+")
    suspend fun deleteDeck(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = update.text.substring(12).toInt()
        subscribeBlocking(client.getDeckById(deckId), {
            editMessageText(update.callbackQuery.message?.messageId!!) {
                "are you sure you want to delete the deck with title '${it.title}'?"
            }.inlineKeyboardMarkup {
                "yes" callback "delete_deck_yes_$deckId"
                "no" callback "deck_$deckId"
            }.send(user, bot)
            answerCallbackQuery(update.callbackQuery.id).send(user.id, bot)
        })
    }

    @RegexCommandHandler(value = "^delete_deck_yes_[0-9]+")
    suspend fun deleteDeckYes(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = update.text.substring(16).toInt()
        subscribeBlocking(client.deleteDeckById(deckId), {
            editMessageText(update.callbackQuery.message?.messageId!!) {
                "deck successfully deleted!"
            }.inlineKeyboardMarkup {
                "⬅\uFE0F  menu" callback "/menu"
            }.send(user, bot)
            answerCallbackQuery(update.callbackQuery.id).send(user.id, bot)
        })
    }

    @RegexCommandHandler(value = "^edit_deck_[0-9]+")
    suspend fun editDeck(update: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = update.text.substring(10).toInt()
        editMessageText(update.callbackQuery.message?.messageId!!) {
            "write new deck title"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back to deck" callback "deck_$deckId"
        }.send(user, bot)
        bot.inputListener[user] = "getDeckTitleUpdate"
        repository.save(Deck(user.id, deckId))
        answerCallbackQuery(update.callbackQuery.id).send(user.id, bot)
    }

    @InputHandler(["getDeckTitleUpdate"])
    suspend fun getDeckTitleUpdate(update: MessageUpdate, user: User, bot: TelegramBot) {
        if (update.text.isNotEmpty()) {
            val (_, deckId) = repository.findById(user.id) ?: throw RuntimeException()
            subscribeBlocking(client.updateDeckById(deckId, DeckRequest(user.id, update.text)), {
                message {
                    "deck updated!"
                }.inlineKeyboardMarkup {
                    "⬅\uFE0F  menu" callback "/menu"
                }.send(user.id, bot)
            })
            deleteMessages(user, bot, update.message, 2)
            return
        }
        message{
            "invalid type for title :("
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  menu" callback "/menu"
        }.send(user.id, bot)
        deleteMessages(user, bot, update.message, 2)
    }
}
