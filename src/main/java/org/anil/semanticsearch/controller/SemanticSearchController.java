package org.anil.semanticsearch.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class SemanticSearchController {

  private VectorStore vectorStore;

  public SemanticSearchController(VectorStore vectorStore){
    this.vectorStore = vectorStore;
  }


  public record BollyWoodMovie (String movieId, String title, String mainLeads, String plotDescription,

                                String distance
                                ){}

  @GetMapping("/searchmovie")
  public List<BollyWoodMovie> getSimilarSearch(@RequestBody String search){
    List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(search)
            .withTopK(5)
            .withFilterExpression("mainLeads == 'Aamir Khan, Gracy Singh'")
    );
    return documents.stream()
            .map(doc ->
                    new BollyWoodMovie(doc.getMetadata().get("movieId").toString(),
                            doc.getMetadata().get("movieName").toString(),
                            doc.getMetadata().get("mainLeads").toString(),
                            doc.getMetadata().get("plotDescription").toString(),
                            doc.getMetadata().get("distance").toString()
                    )
            ).toList();
  }
}
