package memorizer.model.rest

data class DeckResponse (
    val id : Int,
    val owner : Long,
    val title : String
)
