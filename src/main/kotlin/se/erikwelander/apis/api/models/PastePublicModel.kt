package se.erikwelander.apis.api.models

data class PastePublicModel(
        var id: Long? = null,
        var type: Int = 0,
        var hits: Int = 0,
        var title: String = "",
        var language: String = "",
        var data: String = "",
        var created: String = "",
        var location: String = ""
)