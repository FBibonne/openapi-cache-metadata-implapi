package bibonne.exp.oascache.metadata.config;

import com.fasterxml.jackson.core.JsonParser;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public record SwaggerResource(OpenAPI openAPI) {


    public SwaggerResource(@NonNull @Value("${bibonne.metadata.api.openapi-file-path:openapi.yaml}") String openapiInClassPath){
        this(loadOpenAPI(openapiInClassPath));
    }

    private static OpenAPI loadOpenAPI(String openapiInClassPath) {
        try(JsonParser parser=Yaml.mapper().createParser(SwaggerResource.class.getResourceAsStream(openapiInClassPath))){
            return parser.readValueAs(OpenAPI.class);
        }catch (IOException e){
            var openAPIError=new OpenAPI();
            openAPIError.setInfo(new Info().description(STR."Error while parsing oas file spec in jar at \{openapiInClassPath} : \{e}"));
            return openAPIError;
        }
    }

    private OpenAPI copyOfOpenAPI() {
        var retour=new OpenAPI();
        BeanUtils.copyProperties(openAPI, retour);
        return retour;
    }

    private OpenAPI copyOfOpenAPIWithServer(Server server) {
        var retour=copyOfOpenAPI();
        retour.setServers(List.of(server));
        return retour;
    }


    public OpenAPI copyOfOpenAPIWithServer(String url) {
        var server=new Server();
        server.setUrl(url);
        return copyOfOpenAPIWithServer(server);
    }
}
