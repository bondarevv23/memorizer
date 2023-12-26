package memorizer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SpringBot

fun main(args: Array<String>) {
    SpringApplication.run(SpringBot::class.java, *args)
}
