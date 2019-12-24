package se.erikwelander.apis.api.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseStatus (
        val status: Int,
        val message: String,
        val exception: String? = null
)