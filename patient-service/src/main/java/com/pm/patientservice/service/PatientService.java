package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.kafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final kafkaProducer kafkaProducer;


    // dependency injection
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, kafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    // GET All Patients
    // use PatientMapper: convert entity obj to the patienResponseDTO obj
    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        // before using lambda expression ex. patient-> PatientMapper.toDTO(patient)
        List<PatientResponseDTO> patientResponseDTOS = patients.stream().map( PatientMapper::toDTO).toList();

        return patientResponseDTOS;
    }


    // CREATE Patient
    // use PatientMapper: convert the patienResponseDTO obj to entity obj
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){

        // have existing email => can't create
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        // dependency injection
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getFirstName(), newPatient.getLastName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toDTO(newPatient);
    }

    // UPDATE Patient
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with ID: " + id));

        // find a record with this email that does not belong to the given id
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        patient.setFirstName(patientRequestDTO.getFirstName());
        patient.setLastName(patientRequestDTO.getLastName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setBirthDate(LocalDate.parse(patientRequestDTO.getBirthDate()));
        patient.setEmail(patientRequestDTO.getEmail());

        // save to repo
        Patient updatedPatient = patientRepository.save(patient);
        // convert to DTO
        return PatientMapper.toDTO(updatedPatient) ;
    }


    public void deletePatient(UUID id){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with ID: " + id));


        patientRepository.deleteById(id);
    }
}
