package bibonne.exp.oascache.metadata.config;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.core.util.Yaml;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

@Service
// no @EnableWebMVC otherwise, it disables all spring boot auto-config
public record OASCustomizer(SwaggerResource swaggerResource) {

    public static final String openapiResourcePath="oas/v3/openapi.yaml";

    public BufferedWriter customizeWith(@NonNull HttpServletRequest request) {
        var openAPI=swaggerResource.copyOfOpenAPIWithServer(retrieveServerUrlFrom(request).toString());
        try (OutputStream outputstream=new ByteArrayOutputStream()){
            Yaml.mapper().writeValue(outputstream, openAPI);
            return new BufferedWriter(new OutputStreamWriter(outputstream));
        } catch (IOException e) {
            //TODO Handle it
            var writer=new StringWriter();
            writer.write(e.toString());
            return new BufferedWriter(writer);
        }
    }

    private URI retrieveServerUrlFrom(HttpServletRequest request) {
        try {
            var uri=URI.create(request.getRequestURL().toString());
            return new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(), uri.getPort(), null, null, uri.getFragment());
        } catch (URISyntaxException | IllegalArgumentException e) {
            return URI.create("http://localhost:8080");
        }
    }
}
