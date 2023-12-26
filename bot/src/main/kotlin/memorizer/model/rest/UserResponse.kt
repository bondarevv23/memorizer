package memorizer.model.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    @JsonProperty("tgId")
    val id : Long
)
