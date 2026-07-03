package com.atendeai.modules.customer.repository;

import com.atendeai.modules.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Page<Customer> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "c.phone LIKE CONCAT('%', :q, '%'))")
    Page<Customer> search(String q, Pageable pageable);
}
