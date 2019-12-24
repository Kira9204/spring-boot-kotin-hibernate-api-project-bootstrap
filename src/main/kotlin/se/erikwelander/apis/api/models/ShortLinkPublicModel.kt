package se.erikwelander.apis.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ShortLinkPublicModel (
        var id: Long? = null,
        var hits: Int = 0,
        var link: String = "",
        var created: String = "",
        var location: String? = null,
        var redirectLink: String? = null,
        var shortLink: String? = null
)