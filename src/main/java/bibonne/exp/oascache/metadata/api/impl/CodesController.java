package bibonne.exp.oascache.metadata.api.impl;

import bibonne.exp.oascache.metadata.services.SparqlUtils;
import bibonne.exp.oascache.metadata.api.CodesApi;
import bibonne.exp.oascache.metadata.api.model.CategorieJuridiqueNiveauII;
import bibonne.exp.oascache.metadata.api.model.CategorieJuridiqueNiveauIII;
import bibonne.exp.oascache.metadata.api.model.ClasseNAF2008;
import bibonne.exp.oascache.metadata.api.model.SousClasseNAF2008;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public record CodesController(CsvMapper mapper, CsvSchema schemaForCsvResult, SparqlUtils sparqlUtils, String baseHost) implements CodesApi {

    @Autowired
    public CodesController(SparqlUtils sparqlUtils, @Value("${bibonne.metadata.api.baseHost}") String baseHost){
        this(buildCsvMapper(), CsvSchema.emptySchema().withHeader(), sparqlUtils, baseHost);
    }

    private static CsvMapper buildCsvMapper() {
        return CsvMapper.csvBuilder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Override
    public ResponseEntity<CategorieJuridiqueNiveauII> getcjn2(String code) throws IOException {
        var query= STR."""
                SELECT ?uri ?intitule WHERE {
                FILTER(STRENDS(STR(?uri), '\{code}'))
                ?lastCJThirdLevel skos:member ?uri .
                ?uri skos:prefLabel ?intitule
                FILTER (lang(?intitule) = 'fr')
                {
                SELECT ?lastCJThirdLevel WHERE {
                ?lastCJThirdLevel xkos:organizedBy <\{baseHost}/concepts/cj/cjNiveauII> .
                BIND(STRBEFORE(STRAFTER(STR(?lastCJThirdLevel), '\{baseHost}/codes/cj/cj'), '/niveauII') AS ?lastCJVersion)
                BIND(xsd:float(?lastCJVersion) AS ?lastCJVersionFloat)}
                ORDER BY DESC (?lastCJVersionFloat)
                LIMIT 1
                }
                }
                """;
        var csv=sparqlUtils.executeSparqlQuery(query);
        CategorieJuridiqueNiveauII response;
        try(var iterateur= mapper.readerFor(CategorieJuridiqueNiveauII.class).with(schemaForCsvResult).readValues(csv)){
            response=iterateur.hasNext()? (CategorieJuridiqueNiveauII) iterateur.next() :null;
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CategorieJuridiqueNiveauIII> getcjn3(String s) {
        return null;
    }

    @Override
    public ResponseEntity<ClasseNAF2008> getnafr2n4(String s) {
        return null;
    }

    @Override
    public ResponseEntity<SousClasseNAF2008> getnafr2n5(String s) throws Exception {
        return null;
    }
}
