package se.erikwelander.apis

import org.apache.commons.validator.routines.UrlValidator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.ws.rs.core.HttpHeaders
import java.io.IOException
import java.io.InputStream
import org.apache.tika.Tika





class Utils {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val urlValidator = UrlValidator()
        const val BASE = 36

        fun validateUrl(link: String): Boolean {
            return urlValidator.isValid(link)
        }
        fun toShortLinkBase(id: Long): String {
            return id.toString(BASE)
        }
        fun fromShortLinkBase(id: String): Long? {
            return id.toLongOrNull(BASE)
        }
        fun getClientIp(headers: HttpHeaders): String {
            return headers.getRequestHeader("X-Real-IP")[0]
        }
        fun toISODateTime(date: Date): String {
            return dateFormat.format(date)
        }
        fun getCurrentISODateTime(): String {
            return dateFormat.format(Date())
        }

        @Throws(IOException::class)
        fun writeUploadToFile(inputStream: InputStream, desiredLocation: String) {
            try {
                val byteArr: ByteArray = inputStream.readAllBytes()
                File(desiredLocation).writeBytes(byteArr)
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            }

        }

        @Throws(IOException::class)
        fun readTextFile(location: String): String? {
            try {
                val inputStream: InputStream = File(location).inputStream()
                val inputString = inputStream.bufferedReader().use { it.readText() }
                return inputString
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            }
        }

        fun base64EncodeFile(filePath: String): String{
            val bytes = File(filePath).readBytes()
            val base64 = Base64.getEncoder().encodeToString(bytes)
            return base64
        }

        fun base64Decoder(base64Str: String, pathFile: String): Unit{
            val imageByteArray = Base64.getDecoder().decode(base64Str)
            File(pathFile).writeBytes(imageByteArray)
        }

        fun deleteFile(path: String): Boolean {
            return File(path).delete()
        }

        fun getFileType(path: String): String {
            return Tika().detect(File(path))
        }
    }
}
