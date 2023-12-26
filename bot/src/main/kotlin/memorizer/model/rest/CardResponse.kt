package memorizer.model.rest

data class CardResponse (
    val id: Int,
    val deckId: Int,
    val question: SideResponse,
    val answer: SideResponse
)
