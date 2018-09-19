package org.aksw.geiser.ner.fox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.aksw.fox.binding.FoxApi;
import org.aksw.fox.binding.FoxParameter;
import org.aksw.fox.binding.FoxResponse;
import org.aksw.fox.binding.IFoxApi;
import org.aksw.fox.data.Entity;
import org.aksw.geiser.ner.SimpleNERWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FoxNERWrapper implements SimpleNERWrapper {

	@Value("${fox_api_url:http://0.0.0.0:4444/fox}")
	private String foxApiUrl;

	@Override
	public List<IRI> getEntities(String input) throws IOException {
		final IFoxApi fox = new FoxApi().setApiURL(new URL(foxApiUrl)).setTask(FoxParameter.TASK.NER)
				.setOutputFormat(FoxParameter.OUTPUT.JSONLD).setLang(FoxParameter.LANG.DE).setInput(input)
				// .setLightVersion(FoxParameter.FOXLIGHT.ENBalie)
				.send();

		//final String jsonld = fox.responseAsFile();
		final FoxResponse response = fox.responseAsClasses();

		List<Entity> entities = response.getEntities();
		// List<RelationSimple> relations = response.getRelations();
		List<IRI> iris = entities.stream().map(x -> {
			return SimpleValueFactory.getInstance().createIRI(x.getUri());
		}).collect(Collectors.toList());

		return iris;
	}

	/* for testing...
	public void invokeFox() throws MalformedURLException {
		final IFoxApi fox = new FoxApi().setApiURL(new URL("http://0.0.0.0:4444/fox")).setTask(FoxParameter.TASK.NER)
				.setOutputFormat(FoxParameter.OUTPUT.JSONLD).setLang(FoxParameter.LANG.DE)
				.setInput("Angela Merkel hat in Leipzig studiert.")
				// .setLightVersion(FoxParameter.FOXLIGHT.ENBalie)
				.send();

		final String jsonld = fox.responseAsFile();
		final FoxResponse response = fox.responseAsClasses();

		List<Entity> entities = response.getEntities();
		List<RelationSimple> relations = response.getRelations();
	}
	*/

}
