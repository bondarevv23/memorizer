package memorizer.model.redis

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Side (
    var title: String?,
    @JsonProperty("imageLink")
    var image: String?,
    var text: String?
) : Serializable {
    public constructor() : this(null, null, null)
}
