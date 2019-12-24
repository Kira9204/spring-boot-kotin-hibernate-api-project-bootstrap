package se.erikwelander.apis.configuration

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import se.erikwelander.apis.api.ShortLinksResource
import se.erikwelander.apis.api.PasteResource

@Component
class JerseyConfig : ResourceConfig() {
    init {
        // Due to a limitation in Jersey as described here: https://github.com/spring-projects/spring-boot/issues/7496
        // we need to register each resource and provider manually.
        //packages("se.erikwelander.apis.api")

        register(MultiPartFeature::class.java)
        register(CORSFilter::class.java)
        register(ShortLinksResource::class.java)
        register(PasteResource::class.java)
    }

    @Bean
    fun jacksonBuilder() = Jackson2ObjectMapperBuilder()
}