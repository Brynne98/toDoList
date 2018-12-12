package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.ToDoList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ToDoList entity.
 */
public interface ToDoListSearchRepository extends ElasticsearchRepository<ToDoList, Long> {
}
