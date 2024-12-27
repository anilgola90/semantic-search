package org.anil.semanticsearch;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class SemanticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemanticsearchApplication.class, args);
	}


	record BollyWoodMovies (String movieId,
							String movieName,
							String mainLeads,
							String plotDescription
	){}


	boolean ingest = false;
	@Bean
	ApplicationRunner getApplicationRunner(JdbcClient jdbcClient, VectorStore vectorStore){
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				var movies =  jdbcClient.sql("select * from bollywood_movies")
								.query(new DataClassRowMapper<>(BollyWoodMovies.class))
								.list();

				if(ingest) {
					for (BollyWoodMovies movie : movies) {
						Document document = new Document(movie.movieName + " " + movie.mainLeads + " " + movie.plotDescription
										, Map.of("movieId", movie.movieId,
										"movieName", movie.movieName,
										"mainLeads", movie.mainLeads,
										"plotDescription", movie.plotDescription));

						TokenTextSplitter splitter = new TokenTextSplitter();
						List<Document> docs = splitter.apply(List.of(document));
						vectorStore.add(docs);
					}
				}



			}
		};
	}
}
