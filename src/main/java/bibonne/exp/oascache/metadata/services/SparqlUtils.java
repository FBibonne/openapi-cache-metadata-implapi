package bibonne.exp.oascache.metadata.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public record SparqlUtils(RestClient.RequestHeadersUriSpec<?> sparqlClient) {

    @Autowired
    public SparqlUtils(@Value("${bibonne.metadata.api.sparql-end-point}") String sparqlEndPoint) {
        this(RestClient.create(sparqlEndPoint).get());
    }

    private static final String PREFIXES = """
            PREFIX igeo:<http://rdf.insee.fr/def/geo#>
            PREFIX dcterms:<http://purl.org/dc/terms/>
            PREFIX xkos:<http://rdf-vocabulary.ddialliance.org/xkos#>
            PREFIX evoc:<http://eurovoc.europa.eu/schema#>
            PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
            PREFIX dc:<http://purl.org/dc/elements/1.1/>
            PREFIX insee:<http://rdf.insee.fr/def/base#>
            PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX pav:<http://purl.org/pav/>
            PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
            PREFIX prov:<http://www.w3.org/ns/prov#>
            PREFIX sdmx-mm:<http://www.w3.org/ns/sdmx-mm#>
            """;

    public String executeSparqlQuery(@NonNull String query) {
        log.debug("Sent query : {}", query);
        var response=sparqlClient
                .uri("?query={sparqlQuery}", encode(PREFIXES+query))
                .accept(MediaType.valueOf("text/csv"))
                .retrieve()
                .body(String.class);
        log.debug("SPARQL query returned: \n {}", response);
        return response;
    }

    public String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}
