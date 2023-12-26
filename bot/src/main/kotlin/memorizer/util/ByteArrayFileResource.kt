package memorizer.util

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL

class ByteArrayFileResource (
    private val byteArrayResource: ByteArrayResource,
    private val filename: String?
) : Resource {
    public constructor (byteArray: ByteArray, filename: String?) : this(ByteArrayResource(byteArray), filename)

    override fun getInputStream(): InputStream = byteArrayResource.inputStream

    override fun exists(): Boolean = byteArrayResource.exists()

    override fun getURL(): URL = byteArrayResource.url

    override fun getURI(): URI = byteArrayResource.uri

    override fun getFile(): File = byteArrayResource.file

    override fun contentLength(): Long = byteArrayResource.contentLength()

    override fun lastModified(): Long = byteArrayResource.lastModified()

    override fun createRelative(relativePath: String): Resource = byteArrayResource.createRelative(relativePath)

    override fun getFilename(): String? = filename

    override fun getDescription(): String = byteArrayResource.description
}
