package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ToDoListApp;

import com.mycompany.myapp.domain.ToDoList;
import com.mycompany.myapp.repository.ToDoListRepository;
import com.mycompany.myapp.repository.search.ToDoListSearchRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ToDoListResource REST controller.
 *
 * @see ToDoListResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ToDoListApp.class)
public class ToDoListResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    @Autowired
    private ToDoListRepository toDoListRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.ToDoListSearchRepositoryMockConfiguration
     */
    @Autowired
    private ToDoListSearchRepository mockToDoListSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restToDoListMockMvc;

    private ToDoList toDoList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ToDoListResource toDoListResource = new ToDoListResource(toDoListRepository, mockToDoListSearchRepository);
        this.restToDoListMockMvc = MockMvcBuilders.standaloneSetup(toDoListResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToDoList createEntity(EntityManager em) {
        ToDoList toDoList = new ToDoList()
            .title(DEFAULT_TITLE)
            .category(DEFAULT_CATEGORY)
            .description(DEFAULT_DESCRIPTION)
            .dueDate(DEFAULT_DUE_DATE)
            .status(DEFAULT_STATUS);
        return toDoList;
    }

    @Before
    public void initTest() {
        toDoList = createEntity(em);
    }

    @Test
    @Transactional
    public void createToDoList() throws Exception {
        int databaseSizeBeforeCreate = toDoListRepository.findAll().size();

        // Create the ToDoList
        restToDoListMockMvc.perform(post("/api/to-do-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDoList)))
            .andExpect(status().isCreated());

        // Validate the ToDoList in the database
        List<ToDoList> toDoListList = toDoListRepository.findAll();
        assertThat(toDoListList).hasSize(databaseSizeBeforeCreate + 1);
        ToDoList testToDoList = toDoListList.get(toDoListList.size() - 1);
        assertThat(testToDoList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testToDoList.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testToDoList.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testToDoList.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testToDoList.isStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the ToDoList in Elasticsearch
        verify(mockToDoListSearchRepository, times(1)).save(testToDoList);
    }

    @Test
    @Transactional
    public void createToDoListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toDoListRepository.findAll().size();

        // Create the ToDoList with an existing ID
        toDoList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToDoListMockMvc.perform(post("/api/to-do-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDoList)))
            .andExpect(status().isBadRequest());

        // Validate the ToDoList in the database
        List<ToDoList> toDoListList = toDoListRepository.findAll();
        assertThat(toDoListList).hasSize(databaseSizeBeforeCreate);

        // Validate the ToDoList in Elasticsearch
        verify(mockToDoListSearchRepository, times(0)).save(toDoList);
    }

    @Test
    @Transactional
    public void getAllToDoLists() throws Exception {
        // Initialize the database
        toDoListRepository.saveAndFlush(toDoList);

        // Get all the toDoListList
        restToDoListMockMvc.perform(get("/api/to-do-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toDoList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getToDoList() throws Exception {
        // Initialize the database
        toDoListRepository.saveAndFlush(toDoList);

        // Get the toDoList
        restToDoListMockMvc.perform(get("/api/to-do-lists/{id}", toDoList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(toDoList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingToDoList() throws Exception {
        // Get the toDoList
        restToDoListMockMvc.perform(get("/api/to-do-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToDoList() throws Exception {
        // Initialize the database
        toDoListRepository.saveAndFlush(toDoList);

        int databaseSizeBeforeUpdate = toDoListRepository.findAll().size();

        // Update the toDoList
        ToDoList updatedToDoList = toDoListRepository.findById(toDoList.getId()).get();
        // Disconnect from session so that the updates on updatedToDoList are not directly saved in db
        em.detach(updatedToDoList);
        updatedToDoList
            .title(UPDATED_TITLE)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .status(UPDATED_STATUS);

        restToDoListMockMvc.perform(put("/api/to-do-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedToDoList)))
            .andExpect(status().isOk());

        // Validate the ToDoList in the database
        List<ToDoList> toDoListList = toDoListRepository.findAll();
        assertThat(toDoListList).hasSize(databaseSizeBeforeUpdate);
        ToDoList testToDoList = toDoListList.get(toDoListList.size() - 1);
        assertThat(testToDoList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testToDoList.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testToDoList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testToDoList.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testToDoList.isStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the ToDoList in Elasticsearch
        verify(mockToDoListSearchRepository, times(1)).save(testToDoList);
    }

    @Test
    @Transactional
    public void updateNonExistingToDoList() throws Exception {
        int databaseSizeBeforeUpdate = toDoListRepository.findAll().size();

        // Create the ToDoList

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToDoListMockMvc.perform(put("/api/to-do-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDoList)))
            .andExpect(status().isBadRequest());

        // Validate the ToDoList in the database
        List<ToDoList> toDoListList = toDoListRepository.findAll();
        assertThat(toDoListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ToDoList in Elasticsearch
        verify(mockToDoListSearchRepository, times(0)).save(toDoList);
    }

    @Test
    @Transactional
    public void deleteToDoList() throws Exception {
        // Initialize the database
        toDoListRepository.saveAndFlush(toDoList);

        int databaseSizeBeforeDelete = toDoListRepository.findAll().size();

        // Get the toDoList
        restToDoListMockMvc.perform(delete("/api/to-do-lists/{id}", toDoList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ToDoList> toDoListList = toDoListRepository.findAll();
        assertThat(toDoListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ToDoList in Elasticsearch
        verify(mockToDoListSearchRepository, times(1)).deleteById(toDoList.getId());
    }

    @Test
    @Transactional
    public void searchToDoList() throws Exception {
        // Initialize the database
        toDoListRepository.saveAndFlush(toDoList);
        when(mockToDoListSearchRepository.search(queryStringQuery("id:" + toDoList.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(toDoList), PageRequest.of(0, 1), 1));
        // Search the toDoList
        restToDoListMockMvc.perform(get("/api/_search/to-do-lists?query=id:" + toDoList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toDoList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToDoList.class);
        ToDoList toDoList1 = new ToDoList();
        toDoList1.setId(1L);
        ToDoList toDoList2 = new ToDoList();
        toDoList2.setId(toDoList1.getId());
        assertThat(toDoList1).isEqualTo(toDoList2);
        toDoList2.setId(2L);
        assertThat(toDoList1).isNotEqualTo(toDoList2);
        toDoList1.setId(null);
        assertThat(toDoList1).isNotEqualTo(toDoList2);
    }
}
