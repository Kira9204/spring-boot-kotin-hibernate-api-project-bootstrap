package se.erikwelander.apis.api

import org.springframework.data.repository.findByIdOrNull
import se.erikwelander.apis.ApisApplication.Companion.APPLICATION_URL
import se.erikwelander.apis.Utils
import se.erikwelander.apis.api.models.ShortLinkDBModel
import se.erikwelander.apis.api.models.ShortLinkPublicModel
import se.erikwelander.apis.api.repositories.ShortLinksRepository
import se.erikwelander.apis.api.models.ResponseStatus
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/s.d3ff.se")
@Produces(MediaType.APPLICATION_JSON)
class ShortLinksResource {

    @Inject
    lateinit var shortLinksRepository: ShortLinksRepository

    companion object {
        const val BASE = Utils.BASE
        const val SITE = "s.d3ff.se"
        const val BASE_URL = "$APPLICATION_URL/$SITE"
    }

    @GET
    fun getEmptyPage(): Response {
        return Response.ok(
                ResponseStatus(200, "Please provided a BASE$BASE id!")).build()
    }

    @GET
    @Path("/{id}")
    fun getShortLink(@PathParam("id") id: String): Response {
       val dbId = Utils.fromShortLinkBase(id)
       if (dbId == null) {
           return Response.status(400).entity(
                   ResponseStatus(400, "Not a valid BASE$BASE id!")).build()
       }

       val publicModel = toPublicModel(shortLinksRepository.findByIdOrNull(dbId))
       if (publicModel == null) {
           return Response.status(404).entity(ResponseStatus(400, "Id not found!")).build()
       }

        return Response.ok(publicModel).build()
    }

    @GET
    @Path("/r/{id}")
    fun redirectToShortLink(@PathParam("id") id: String): Response {
        val dbId = Utils.fromShortLinkBase(id)
        if (dbId == null) {
            return Response.status(400).entity(
                    ResponseStatus(400, "Not a valid BASE$BASE id!")).build()
        }

        val publicModel = toPublicModel(shortLinksRepository.findByIdOrNull(dbId))
        if (publicModel == null) {
            return Response.status(404).entity(ResponseStatus(404, "Id not found!")).build()
        }

        shortLinksRepository.increaseHitCount(dbId)
        return Response.status(307).header("Location", publicModel.link).build()
    }

    @POST
    fun insertNewShortLink(@Context headers: HttpHeaders, linkModel: ShortLinkPublicModel): Response {
        if (linkModel.link.isNullOrBlank() || !Utils.validateUrl((linkModel.link))) {
            return Response.status(400).entity(
                    ResponseStatus(400, "This is not a valid link!")).build()
        }

        var dbModel = toDbModel(linkModel, headers)
        dbModel = shortLinksRepository.saveAndFlush(dbModel)

        val publicModel = toPublicModel(dbModel)
        return Response.status(201).header("Location", publicModel!!.location).entity(publicModel).build()
    }

    private fun toPublicModel(dbModel: ShortLinkDBModel?): ShortLinkPublicModel? {
        if (dbModel == null) {
            return null
        }
        val model = ShortLinkPublicModel()
        model.id = dbModel.id
        model.hits = dbModel.hits
        model.link = dbModel.link
        model.created = Utils.toISODateTime(dbModel.created)
        model.location = toLocationLink(dbModel.id!!)
        model.redirectLink = toRedirectLink(dbModel.id!!)
        model.shortLink = toShortLink(dbModel.id!!)

        return model
    }
    private fun toDbModel(publicModel: ShortLinkPublicModel, headers: HttpHeaders): ShortLinkDBModel {
        return ShortLinkDBModel(
                link = publicModel.link,
                ip = Utils.getClientIp(headers)
        )
    }

    private fun toLocationLink(id: Long): String {
        val newBase = Utils.toShortLinkBase(id)
        return "$BASE_URL/$newBase"
    }
    private fun toRedirectLink(id: Long): String {
        val newBase = Utils.toShortLinkBase(id)
        return "$BASE_URL/r/$newBase"
    }
    private fun toShortLink(id: Long): String {
        val newBase = Utils.toShortLinkBase(id)
        return "https://$SITE/$newBase"
    }
}