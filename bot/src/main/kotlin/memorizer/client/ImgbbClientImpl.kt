package memorizer.client

import memorizer.model.imgbb.UploadResponse
import memorizer.util.ByteArrayFileResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.io.File
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path


@Component
class ImgbbClientImpl (
    private val imgbbWebClient: WebClient,
    @Value("\${imgbb.api-key}")
    private val apiKey: String
) : ImgbbClient {
    companion object {
        private const val URI = "/1/upload";
    }

    override fun upload(resource: ByteArrayFileResource): Mono<String> {
        val builder = MultipartBodyBuilder()
        builder.part("image", resource, MediaType.parseMediaType(Files.probeContentType(Path.of(resource.filename))))
        return imgbbWebClient.post()
            .uri {
                it.path(URI)
                    .queryParam("key", apiKey)
                    .build()
            }
            .contentType(MULTIPART_FORM_DATA)
            .body(
                BodyInserters.fromMultipartData(
                    builder.build()
                )
            )
            .retrieve()
            .bodyToMono<UploadResponse>().map{
                it.data.image.url
            }
    }
}