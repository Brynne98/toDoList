package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ToDoList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ToDoList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {

}
