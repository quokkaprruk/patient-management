package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

//passing the Patient Model
//use JpaRepository to do CRUD
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // check if email already exists
    boolean existsByEmail(String email);
    // check if email exists but belongs to someone
    //[action]By[Property][Operator]And[Property][Operator]...
    boolean existsByEmailAndIdNot(String email, UUID id);

}
