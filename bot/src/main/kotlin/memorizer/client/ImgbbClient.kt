package memorizer.client

import memorizer.util.ByteArrayFileResource
import reactor.core.publisher.Mono

interface ImgbbClient {
    fun upload(resource: ByteArrayFileResource) : Mono<String>
}
