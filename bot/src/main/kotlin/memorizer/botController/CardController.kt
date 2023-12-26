package memorizer.botController

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.annotations.RegexCommandHandler
import eu.vendeli.tgbot.api.deleteMessage
import eu.vendeli.tgbot.api.editMessageText
import eu.vendeli.tgbot.api.getFile
import eu.vendeli.tgbot.api.media.sendPhoto
import eu.vendeli.tgbot.api.sendMessage
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.CallbackQueryUpdate
import eu.vendeli.tgbot.types.internal.CommandScope
import eu.vendeli.tgbot.types.internal.MessageUpdate
import eu.vendeli.tgbot.types.internal.getOrNull
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardButton
import eu.vendeli.tgbot.types.keyboard.InlineKeyboardMarkup
import memorizer.botController.ControllerUtilities.deleteMessages
import memorizer.botController.ControllerUtilities.subscribeBlocking
import memorizer.client.ImgbbClient
import memorizer.client.MemorizerClient
import memorizer.model.redis.Card
import memorizer.model.redis.Side
import memorizer.model.rest.CardResponse
import memorizer.repository.CardRepository
import memorizer.repository.CardRepositoryImpl
import memorizer.util.ByteArrayFileResource
import org.springframework.stereotype.Component

@Component
class CardController (
    private val client: MemorizerClient,
    private val imgClient: ImgbbClient,
    private val repository: CardRepository
) {
    companion object {
        enum class Side {
            QUESTION,
            ANSWER;

            companion object {
                public fun parseCallBack(callBackText : String) : Side =
                    if (ANSWER.lowarcase() in callBackText.lowercase()) ANSWER else QUESTION
            }

            public fun lowarcase() : String = this.toString().lowercase()

            public fun other() : Side = if (this == ANSWER) QUESTION else ANSWER

            public fun title() : String = lowarcase().replaceFirstChar(Char::titlecaseChar)

            public fun get(card: Card) = if (this == ANSWER) card.answer else card.question

            public fun get(card: CardResponse) = if (this == ANSWER) card.answer else card.question
        }
    }

    @RegexCommandHandler(value = "^add_card_[0-9]+")
    public suspend fun createCard(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = callback.text.substring(9).toInt()
        repository.save(Card(user.id, deckId))
        bot.inputListener[user] = "getQuestionTitle"
        editMessageText(callback.callbackQuery.message?.messageId!!) {
            "write a title of question side"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  menu" callback "/menu"
        }.send(user, bot)
    }

    @InputHandler(["getQuestionTitle"])
    public suspend fun getQuestionTitle(message: MessageUpdate, user: User, bot: TelegramBot) =
        getTitle(message, user, bot, Side.QUESTION)

    @InputHandler(["getAnswerTitle"])
    public suspend fun getAnswerTitle(message: MessageUpdate, user: User, bot: TelegramBot) =
        getTitle(message, user, bot, Side.ANSWER)

    @CommandHandler(value = ["custom_question", "custom_answer"], scope = [CommandScope.CALLBACK])
    public suspend fun customSide(
        sideReq: Side?,
        user: User,
        bot: TelegramBot,
        callback: CallbackQueryUpdate? = null) {
        val side = if (callback != null) {
            deleteMessage(callback.callbackQuery.message?.messageId!!).send(user, bot)
            Side.parseCallBack(callback.text)
        } else {
            sideReq!!
        }
        val card = repository.findById(user.id)!!
        val update = card.id != null
        val cardSide = side.get(card)
        val text = if (cardSide.text == null) "*${cardSide.title}*" else "*${cardSide.title}*\n\n${cardSide.text}"
        val keyboard = InlineKeyboardMarkup(
            listOf(
                listOf(InlineKeyboardButton(text = "edit title", callbackData = "${side.lowarcase()}_set_title")),
                listOf(InlineKeyboardButton(text = "edit text", callbackData = "${side.lowarcase()}_set_text")),
                listOf(InlineKeyboardButton(text = "edit image", callbackData = "${side.lowarcase()}_set_image")),
                if (!update)
                    listOf(
                        InlineKeyboardButton(text = "⬅\uFE0F  menu", callbackData = "/menu"),
                        InlineKeyboardButton(text = "\uD83D\uDCBE  save", callbackData = "save_${side.lowarcase()}")
                    )
                else
                    listOf(
                        InlineKeyboardButton(
                            text = "get ${side.other().lowarcase()}",
                            callbackData = "update_$${side.other().lowarcase()}"
                        ),
                        InlineKeyboardButton(text = "\uD83D\uDD04  update", callbackData = "update_card")
                    ),
                if (update) listOf(InlineKeyboardButton(text = "⬅\uFE0F  menu", callbackData = "/menu")) else listOf()
            )
        )
        if (cardSide.image != null) {
            sendPhoto {
                cardSide.image!!
            }.caption {
                text
            }.options { parseMode = ParseMode.Markdown }.markup {
                keyboard
            }.send(user, bot)
            return
        }
        sendMessage {
            text
        }.options { parseMode = ParseMode.Markdown }.markup {
            keyboard
        }.send(user, bot)
    }

    @RegexCommandHandler(value = "^question_set_title$")
    public suspend fun questionSetTitle(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setTitle(callback, user, bot, Side.QUESTION)

    @RegexCommandHandler(value = "^answer_set_title$")
    public suspend fun answerSetTitle(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setTitle(callback, user, bot, Side.ANSWER)

    private suspend fun setTitle(callback: CallbackQueryUpdate, user: User, bot: TelegramBot, side: Side) {
        sendMessage {
            "write ${side.lowarcase()} title"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back" callback "custom_${side.lowarcase()}"
        }.send(user, bot)
        bot.inputListener[user] = "get${side.title()}Title"
        deleteMessage(callback.callbackQuery.message?.messageId!!).send(user, bot)
    }

    @RegexCommandHandler(value = "^question_set_text$")
    public suspend fun questionSetText(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setText(callback, user, bot, Side.QUESTION)

    @RegexCommandHandler(value = "^answer_set_text$")
    public suspend fun answerSetText(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setText(callback, user, bot, Side.ANSWER)

    private suspend fun setText(callback: CallbackQueryUpdate, user: User, bot: TelegramBot, side: Side) {
        sendMessage {
            "write ${side.lowarcase()} text"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back to editor" callback "custom_${side.lowarcase()}"
        }.send(user, bot)
        bot.inputListener[user] = "get${side.title()}Text"
        deleteMessage(callback.callbackQuery.message?.messageId!!).send(user, bot)
    }

    @InputHandler(["getQuestionText"])
    public suspend fun getQuestionText(message: MessageUpdate, user: User, bot: TelegramBot) =
        getText(message, user, bot, Side.QUESTION)

    @InputHandler(["getAnswerText"])
    public suspend fun getAnswerText(message: MessageUpdate, user: User, bot: TelegramBot) =
        getText(message, user, bot, Side.ANSWER)

    private suspend fun getTitle(message: MessageUpdate, user: User, bot: TelegramBot, sideEnum: Side) =
        getTextFromUser(message, user, bot, sideEnum, "title") {
                side: memorizer.model.redis.Side, title: String -> side.title = title
        }

    private suspend fun getText(message: MessageUpdate, user: User, bot: TelegramBot, sideEnum: Side) =
        getTextFromUser(message, user, bot, sideEnum, "text") {
            side: memorizer.model.redis.Side, text: String -> side.text = text
        }

    private suspend fun getTextFromUser(
        message: MessageUpdate,
        user: User,
        bot: TelegramBot,
        side: Side,
        string: String,
        setter: (memorizer.model.redis.Side, String) -> Unit
    ) {
        if (message.text.isNotEmpty()) {
            val card = repository.findById(user.id)!!
            setter(side.get(card), message.text)
            repository.save(card)
            customSide(side, user, bot)
            deleteMessages(user, bot, message.message, 2)
            return
        }
        sendMessage {
            "invalid $string passed"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back to card editor" callback "custom_${side.lowarcase()}"
        }.send(user, bot)
        deleteMessages(user, bot, message.message, 2)
    }

    @RegexCommandHandler(value = "^question_set_image$")
    public suspend fun questionSetImage(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setImage(callback, user, bot, Side.QUESTION)

    @RegexCommandHandler(value = "^answer_set_image$")
    public suspend fun answerSetImage(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        setImage(callback, user, bot, Side.ANSWER)

    private suspend fun setImage(callback: CallbackQueryUpdate, user: User, bot: TelegramBot, side: Side) {
        sendMessage {
            "send image"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back to editor" callback "custom_${side.lowarcase()}"
        }.send(user, bot)
        bot.inputListener[user] = "get${side.title()}Image"
        deleteMessage(callback.callbackQuery.message?.messageId!!).send(user, bot)
    }

    @InputHandler(["getQuestionImage"])
    public suspend fun getQuestionImage(message: MessageUpdate, user: User, bot: TelegramBot) =
        getImage(message, user, bot, Side.QUESTION)

    @InputHandler(["getAnswerImage"])
    public suspend fun getAnswerImage(message: MessageUpdate, user: User, bot: TelegramBot) =
        getImage(message, user, bot, Side.ANSWER)

    private suspend fun getImage(message: MessageUpdate, user: User, bot: TelegramBot, side: Side) {
        val photos = message.message.photo
        if (!photos.isNullOrEmpty()) {
            val file = getFile(photos.last().fileId).sendAsync(bot).await().getOrNull()
            if (file != null) {
                val fileContent = bot.getFileContent(file)
                val extension = bot.getFileDirectUrl(file)?.split(".")?.last()
                if (fileContent != null && extension != null) {
                    subscribeBlocking(imgClient.upload(ByteArrayFileResource(
                        fileContent, "${file.fileUniqueId}.$extension"
                    )), {
                        val card = repository.findById(user.id)!!
                        side.get(card).image = it
                        repository.save(card)
                        customSide(side, user, bot)
                        deleteMessages(user, bot, message.message, 2)
                    })
                    return
                }
            }
        }
        sendMessage {
            "invalid image passed"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  back to card editor" callback "custom_${side.lowarcase()}"
        }.send(user, bot)
        deleteMessages(user, bot, message.message, 2)
    }

    @RegexCommandHandler(value = "^save_question$")
    public suspend fun saveQuestion(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        bot.inputListener[user] = "getAnswerTitle"
        sendMessage {
            "write a title of answer side"
        }.inlineKeyboardMarkup {
            "⬅\uFE0F  menu" callback "/menu"
        }.send(user, bot)
        deleteMessage(callback.callbackQuery.message?.messageId!!).send(user, bot)
    }

    @RegexCommandHandler(value = "^save_answer$")
    public suspend fun saveAnswer(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val card = repository.findById(user.id) ?: return
        repository.deleteById(user.id)
        subscribeBlocking(client.addNewCard(card), {
            sendMessage {
                "new card saved!"
            }.inlineKeyboardMarkup {
                "⬅\uFE0F  menu" callback "/menu"
            }.send(user, bot)
            deleteMessages(user, bot, callback.callbackQuery.message!!, 2)
        })
    }

    @RegexCommandHandler(value = "^card_[0-9]+$")
    public suspend fun getCard(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val cardId = callback.text.substring(5).toInt()
        subscribeBlocking(client.getCardById(cardId), {
            printCardSide(user, bot, it)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }

    private suspend fun printCardSide(user: User, bot: TelegramBot, card: CardResponse, sideEnum: Side = Side.QUESTION) {
        val side = sideEnum.get(card)
        val text = "*${side.title}*\n\n${side.text ?: ""}"
        val keyboard = InlineKeyboardMarkup(listOf(
            listOf(if (sideEnum == Side.QUESTION)
                InlineKeyboardButton(text = "\uD83D\uDC40  see answer", callbackData = "see_answer_${card.id}")
            else InlineKeyboardButton(text = "\uD83D\uDC40  see question", callbackData = "see_question_${card.id}")),
            listOf(InlineKeyboardButton(text = "\uD83C\uDFB2  next random card", callbackData = "random_card_${card.deckId}")),
            listOf(
                InlineKeyboardButton(text = "\uD83D\uDDD1  delete card", callbackData = "delete_card_${card.id}"),
                InlineKeyboardButton(text = "✏\uFE0F  edit card", callbackData = "update_card_${card.id}")
            ),
            listOf(InlineKeyboardButton(text = "⬅\uFE0F  back to deck", callbackData = "deck_${card.deckId}"))
        ))
        if (side.image != null) {
            sendPhoto {
                side.image
            }.caption {
                text
            }.options { parseMode = ParseMode.Markdown }.markup {
                keyboard
            }
        } else {
            sendMessage {
                text
            }.options { parseMode = ParseMode.Markdown }.markup {
                keyboard
            }
        }.send(user, bot)
    }

    @RegexCommandHandler(value = "^see_answer_[0-9]+$")
    public suspend fun seeAnswer(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val cardId = callback.text.substring(11).toInt()
        subscribeBlocking(client.getCardById(cardId), {
            printCardSide(user, bot, it, Side.ANSWER)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }

    @RegexCommandHandler(value = "^see_question_[0-9]+$")
    public suspend fun seeQuestion(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val cardId = callback.text.substring(13).toInt()
        subscribeBlocking(client.getCardById(cardId), {
            printCardSide(user, bot, it)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }

    @RegexCommandHandler(value = "^random_card_[0-9]+$")
    public suspend fun getRandomCard(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val deckId = callback.text.substring(12).toInt()
        subscribeBlocking(client.getRandomCardByDeckId(deckId), {
            printCardSide(user, bot, it)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        }) {
            sendMessage {
                "the deck is empty :("
            }.inlineKeyboardMarkup {
                "➕  add new card" callback "add_card_$deckId"
                newLine()
                "⬅\uFE0F  back to deck" callback "deck_$deckId"
                newLine()
                "⬅\uFE0F  menu" callback "/menu"
            }.send(user, bot)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        }
    }

    @RegexCommandHandler(value = "^update_card_[0-9]+$")
    public suspend fun updateCardById(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val cardId = callback.text.substring(12).toInt()
        subscribeBlocking(client.getCardById(cardId), {
            repository.save(Card(user.id, it.deckId,
                Side(it.question.title, it.question.image, it.question.text),
                Side(it.answer.title, it.answer.image, it.answer.text),
                cardId
            ))
            customSide(Side.QUESTION, user, bot, null)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }

    @RegexCommandHandler(value = "^update_card$")
    public suspend fun updateCard(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val card = repository.findById(user.id) ?: return
        subscribeBlocking(client.updateCardById(card.id!!, card), {
            sendMessage {
                "card updated!"
            }.inlineKeyboardMarkup {
                "⬅\uFE0F  back to deck" callback "deck_${card.deckId}"
                newLine()
                "⬅\uFE0F  menu" callback "/menu"
            }.send(user, bot)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }

    @RegexCommandHandler(value = "^update_question$")
    public suspend fun updateQuestion(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        updateSide(callback, user, bot, Side.QUESTION)

    @RegexCommandHandler(value = "^update_answer$")
    public suspend fun updateAnswer(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) =
        updateSide(callback, user, bot, Side.ANSWER)

    private suspend fun updateSide(callback: CallbackQueryUpdate, user: User, bot: TelegramBot, side: Side) {
        customSide(side, user, bot, null)
        deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
    }

    @RegexCommandHandler(value = "^delete_card_[0-9]+$")
    public suspend fun deleteCard(callback: CallbackQueryUpdate, user: User, bot: TelegramBot) {
        val cardId = callback.text.substring(12).toInt()
        subscribeBlocking(client.deleteCardById(cardId), {
            sendMessage {
                "card deleted!"
            }.inlineKeyboardMarkup {
                "⬅\uFE0F  menu" callback "/menu"
            }.send(user, bot)
            deleteMessage(callback.callbackQuery.message!!.messageId).send(user, bot)
        })
    }
}
