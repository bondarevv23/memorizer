package memorizer.model.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class SideResponse (
    val title: String,
    val text: String?,
    @JsonProperty("imageLink")
    val image: String?
)
