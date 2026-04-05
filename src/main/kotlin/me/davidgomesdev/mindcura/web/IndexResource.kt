package me.davidgomesdev.mindcura.web

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.UriInfo


@Path("/")
class IndexResource(
    @param:Location("index.html") val index: Template,
) {

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(uriInfo: UriInfo): TemplateInstance {
        val baseUrl = uriInfo.baseUri.run { "$scheme://$host:$port" }
        val apiUrl = "$baseUrl/chat"

        return index.data("API_URL", apiUrl)
    }
}

