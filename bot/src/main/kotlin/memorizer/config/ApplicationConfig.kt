package memorizer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
open class ApplicationConfig (
    @Value("\${server-uri}")
    val serverUri: String,
    @Value("\${imgbb.uri}")
    val imgbbUri: String
) {
    @Bean
    open fun memorizerWebClient() : WebClient = WebClient.create(serverUri)

    @Bean
    open fun imgbbWebClient() : WebClient = WebClient.create(imgbbUri);
}
