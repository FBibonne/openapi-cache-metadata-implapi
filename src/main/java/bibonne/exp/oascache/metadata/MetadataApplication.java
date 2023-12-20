package bibonne.exp.oascache.metadata;

import bibonne.exp.oascache.metadata.config.PropertiesLogger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MetadataApplication {

	public static void main(String[] args) {
		configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);        }

	public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
		return springApplicationBuilder.sources(MetadataApplication.class)
				.listeners(new PropertiesLogger());
	}
}
