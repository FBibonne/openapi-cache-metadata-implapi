package bibonne.exp.oascache.metadata;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;

import java.util.Map;
import java.util.function.Predicate;


public class Bug {

    public static void main(){
        /*var b=WithIllegalFieldName.TRUE;
        System.out.println(b.test.test("false"));*/

        System.out.println((Map<String, Object>)(new Yaml()).load(Bug.class.getClassLoader().getResourceAsStream("openapi.yaml")));
    }


    private enum WithIllegalFieldName{
        TRUE(_ -> true),
        FALSE(_ -> false),
        LOWER(s -> s!=null && s.toLowerCase().equals(s));

        private Predicate<String> test;

        WithIllegalFieldName(Predicate<String> t){
            test=t;
        }
    }
}
