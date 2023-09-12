package com.example.ESDemo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.ESDemo.entity.NewsHeadlines;
import com.example.ESDemo.repository.NewsHeadlinesRepository;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class NewsHeadlinesService {
    @Autowired
    private NewsHeadlinesRepository newsHeadlinesRepository;

    public Iterable<NewsHeadlines> getAllHeadlines(){
        return newsHeadlinesRepository.findAll();
    }

    public NewsHeadlines insertHeadLine(NewsHeadlines newsHeadlines){
        return newsHeadlinesRepository.save(newsHeadlines);
    }

    public List<NewsHeadlines> dynamicQuery() throws IllegalAccessException, IOException {
        // dummyList of expected arguments
        NewsHeadlines headline1 = NewsHeadlines.
                builder()
                .headline("Headline1")
                .authors("ABC")
                .build();
        NewsHeadlines headline2 = NewsHeadlines.
                builder()
                .headline("Headline2")
                .authors("Joseph")
                .build();
        List<NewsHeadlines> headlinesList = new ArrayList<>();
        headlinesList.add(headline1);
        headlinesList.add(headline2);

        RestClient restClient = RestClient
                .builder(HttpHost.create("http://localhost:9200"))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        // 1. create a must query for each object in the list
        // 2. create a term query for every field inside a particular object
        // 3. append group of term queries to must query
        // 4. append group of must queries to a should query
        List<Query> mustQueryList = new ArrayList<>();
        for(NewsHeadlines headline : headlinesList){
            Field[] fields = headline.getClass().getDeclaredFields();
            Map<String,Object> map = new HashMap<>();
            List<Query> termQueryList = new ArrayList<>();
            for(Field particularField: fields){
                particularField.setAccessible(true); // To access private fields
                String fieldName = particularField.getName(); // FieldName
                Object fieldValue = null;
                try {
                    if(Objects.nonNull(particularField.get(headline))){
                        fieldValue = particularField.get(headline); // FieldValue
                        // Map<FieldName, It's value>
                        map.put(fieldName, fieldValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            // Iterate through the map containing field name and values
            // create term query for every field, add it to a list
            for (String key: map.keySet()) {
                Query termQuery = TermQuery.of(m -> m.caseInsensitive(true).field(key).value(map.get(key).toString()))._toQuery();
                termQueryList.add(termQuery);
            }
            // Create a  must query
            // append the term queries to must query
            Query mustQuery = BoolQuery.of(m->m.must(termQueryList))._toQuery();
            mustQueryList.add(mustQuery);
        }
        // append must queries to a should query
        SearchResponse<NewsHeadlines> searchResponse = client.search(
                s-> s.index("headlines").query(
                                q-> q.bool(
                                        b->b.should(mustQueryList))), NewsHeadlines.class);

        List<NewsHeadlines> result = new ArrayList<>();
        searchResponse.hits().hits().forEach(hit->result.add(hit.source()));
        return result;
    }
}
