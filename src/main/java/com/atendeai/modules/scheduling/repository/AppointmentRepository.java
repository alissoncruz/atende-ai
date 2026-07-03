package com.atendeai.modules.scheduling.repository;

import com.atendeai.modules.scheduling.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByCustomer_Id(UUID customerId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.scheduledAt BETWEEN :from AND :to ORDER BY a.scheduledAt")
    List<Appointment> findSchedule(Instant from, Instant to);

    @Query("SELECT a FROM Appointment a WHERE DATE(a.scheduledAt) = DATE(:date) ORDER BY a.scheduledAt")
    List<Appointment> findByDate(Instant date);
}
