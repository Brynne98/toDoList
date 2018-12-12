package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.ToDoList;
import com.mycompany.myapp.repository.ToDoListRepository;
import com.mycompany.myapp.repository.search.ToDoListSearchRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ToDoList.
 */
@RestController
@RequestMapping("/api")
public class ToDoListResource {

    private final Logger log = LoggerFactory.getLogger(ToDoListResource.class);

    private static final String ENTITY_NAME = "toDoList";

    private final ToDoListRepository toDoListRepository;

    private final ToDoListSearchRepository toDoListSearchRepository;

    public ToDoListResource(ToDoListRepository toDoListRepository, ToDoListSearchRepository toDoListSearchRepository) {
        this.toDoListRepository = toDoListRepository;
        this.toDoListSearchRepository = toDoListSearchRepository;
    }

    /**
     * POST  /to-do-lists : Create a new toDoList.
     *
     * @param toDoList the toDoList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new toDoList, or with status 400 (Bad Request) if the toDoList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/to-do-lists")
    @Timed
    public ResponseEntity<ToDoList> createToDoList(@RequestBody ToDoList toDoList) throws URISyntaxException {
        log.debug("REST request to save ToDoList : {}", toDoList);
        if (toDoList.getId() != null) {
            throw new BadRequestAlertException("A new toDoList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ToDoList result = toDoListRepository.save(toDoList);
        toDoListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/to-do-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /to-do-lists : Updates an existing toDoList.
     *
     * @param toDoList the toDoList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated toDoList,
     * or with status 400 (Bad Request) if the toDoList is not valid,
     * or with status 500 (Internal Server Error) if the toDoList couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/to-do-lists")
    @Timed
    public ResponseEntity<ToDoList> updateToDoList(@RequestBody ToDoList toDoList) throws URISyntaxException {
        log.debug("REST request to update ToDoList : {}", toDoList);
        if (toDoList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ToDoList result = toDoListRepository.save(toDoList);
        toDoListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, toDoList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /to-do-lists : get all the toDoLists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of toDoLists in body
     */
    @GetMapping("/to-do-lists")
    @Timed
    public ResponseEntity<List<ToDoList>> getAllToDoLists(Pageable pageable) {
        log.debug("REST request to get a page of ToDoLists");
        Page<ToDoList> page = toDoListRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/to-do-lists");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /to-do-lists/:id : get the "id" toDoList.
     *
     * @param id the id of the toDoList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the toDoList, or with status 404 (Not Found)
     */
    @GetMapping("/to-do-lists/{id}")
    @Timed
    public ResponseEntity<ToDoList> getToDoList(@PathVariable Long id) {
        log.debug("REST request to get ToDoList : {}", id);
        Optional<ToDoList> toDoList = toDoListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(toDoList);
    }

    /**
     * DELETE  /to-do-lists/:id : delete the "id" toDoList.
     *
     * @param id the id of the toDoList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/to-do-lists/{id}")
    @Timed
    public ResponseEntity<Void> deleteToDoList(@PathVariable Long id) {
        log.debug("REST request to delete ToDoList : {}", id);

        toDoListRepository.deleteById(id);
        toDoListSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/to-do-lists?query=:query : search for the toDoList corresponding
     * to the query.
     *
     * @param query the query of the toDoList search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/to-do-lists")
    @Timed
    public ResponseEntity<List<ToDoList>> searchToDoLists(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ToDoLists for query {}", query);
        Page<ToDoList> page = toDoListSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/to-do-lists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
