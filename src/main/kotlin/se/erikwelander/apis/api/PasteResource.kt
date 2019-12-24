package se.erikwelander.apis.api

import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataParam
import se.erikwelander.apis.ApisApplication
import se.erikwelander.apis.ApisApplication.Companion.UPLOAD_PATH
import se.erikwelander.apis.Utils
import se.erikwelander.apis.api.models.PasteDbModel
import se.erikwelander.apis.api.models.PastePublicModel
import se.erikwelander.apis.api.repositories.PasteRepository
import java.io.InputStream
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import se.erikwelander.apis.api.models.ResponseStatus
import java.io.IOException

@Path("/paste.d3ff.se")
@Produces(MediaType.APPLICATION_JSON)
class PasteResource {

    @Inject
    lateinit var pasteRepository: PasteRepository

    companion object {
        const val BASE = Utils.BASE
        const val SITE = "paste.d3ff.se"
        const val BASE_URL = "${ApisApplication.APPLICATION_URL}/$SITE"
        const val UPLOAD_DIR = "$UPLOAD_PATH/$SITE"
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
    }

    @GET
    fun getEmptyPage(): Response {
        return Response.ok(ResponseStatus(200, "Please provided a BASE$BASE id!")).build()
    }


    @GET
    @Path("/{id}")
    fun getShortLink(@PathParam("id") id: String): Response {
        val dbId = Utils.fromShortLinkBase(id)
        if (dbId == null) {
            return Response.status(400).entity(
                    ResponseStatus(400, "Not a valid BASE$BASE id!")).build()
        }
        if(!pasteRepository.existsById(dbId)) {
            return Response.status(404).entity(ResponseStatus(404, "Id not found!")).build()
        }

        pasteRepository.increaseHitCount(dbId)
        val publicModel = toPublicModel(pasteRepository.getOne(dbId))

        return Response.ok(publicModel).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun insertNewPasteJSON(@Context headers: HttpHeaders, pastePublicModel: PastePublicModel): Response {
        if (pastePublicModel.data.isNullOrBlank() || pastePublicModel.title.isNullOrBlank() || pastePublicModel.language.isNullOrBlank()) {
            return Response.status(400).entity(
                    ResponseStatus(400, "Missing data, title or language")).build()
        }

        var dbModel = toDbModel(pastePublicModel, headers)
        dbModel = pasteRepository.saveAndFlush(dbModel)

        val publicModel = toPublicModel(dbModel)
        return Response.status(201).header("Location", publicModel!!.location).entity(publicModel).build()
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun insertNewPasteUpload(@Context headers: HttpHeaders,
                             @FormDataParam("file") inputStream: InputStream,
                             @FormDataParam("file") fileDetail: FormDataContentDisposition): Response {

        val fileName = ""+System.currentTimeMillis()+"_"+fileDetail.fileName
        val location = "$UPLOAD_DIR/$fileName"
        try {
            Utils.writeUploadToFile(inputStream, location)
        } catch (e: IOException) {
            return Response.status(500).entity(
                    ResponseStatus(500, "Could not write file to filesystem",
                            e.message)).build()
        }

        val fileType = Utils.getFileType(location)
        if (fileType.startsWith("text")) {
            val data = Utils.readTextFile(location)
            Utils.deleteFile(location)

            val storeModel = PastePublicModel()
            storeModel.type = TYPE_TEXT
            storeModel.title = fileDetail.fileName
            storeModel.data = data!!

            var dbModel = toDbModel(storeModel, headers)
            dbModel = pasteRepository.saveAndFlush(dbModel)

            val publicModel = toPublicModel(dbModel)
            return Response.status(201).entity(publicModel).build()

        } else if (fileType.startsWith("image")) {
            val storeModel = PastePublicModel()
            storeModel.data = fileName
            storeModel.type = TYPE_IMAGE
            storeModel.title = fileDetail.fileName

            var dbModel = toDbModel(storeModel, headers)
            dbModel = pasteRepository.saveAndFlush(dbModel)

            val publicModel = toPublicModel(dbModel)

            return Response.status(201).entity(publicModel).build()
        }
        Utils.deleteFile(location)
        return Response.status(400).entity(
                ResponseStatus(400, "Only text files or images are allowed")).build()
    }

    private fun toPublicModel(dbModel: PasteDbModel?): PastePublicModel? {
        if (dbModel == null) {
            return null
        }
        val model = PastePublicModel()
        model.id = dbModel.id
        model.hits = dbModel.hits
        model.type = dbModel.type
        model.title = dbModel.title
        model.language = dbModel.language
        model.data = dbModel.data
        model.created = Utils.toISODateTime(dbModel.created)
        model.location = toLocationLink(dbModel.id!!)

        if (model.type == TYPE_IMAGE) {
            val filePath = UPLOAD_DIR +"/"+model.data
            val type = Utils.getFileType(filePath)
            val base64 = Utils.base64EncodeFile(filePath)
            model.data = "data:$type;base64,$base64"
        }
        return model
    }

    private fun toDbModel(publicModel: PastePublicModel, headers: HttpHeaders): PasteDbModel {
        return PasteDbModel(
                title = publicModel.title,
                data = publicModel.data,
                ip = Utils.getClientIp(headers),
                type = publicModel.type

        )
    }
    private fun toLocationLink(id: Long): String {
        val newBase = Utils.toShortLinkBase(id)
        return "$BASE_URL/$newBase"
    }
}