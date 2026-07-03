package com.atendeai.modules.tither.repository;

import com.atendeai.modules.tither.model.Tither;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TitherRepository extends JpaRepository<Tither, UUID> {

    Page<Tither> findByActiveTrue(Pageable pageable);

    Page<Tither> findByActiveTrueAndChurch_Id(UUID churchId, Pageable pageable);

    @Query("SELECT t FROM Tither t WHERE t.active = true AND " +
           "(:churchId IS NULL OR t.church.id = :churchId) AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR t.cpf LIKE CONCAT('%', :q, '%'))")
    Page<Tither> search(String q, UUID churchId, Pageable pageable);

    List<Tither> findByActiveTrueAndChurch_IdOrderByNameAsc(UUID churchId);

    List<Tither> findByActiveTrueOrderByNameAsc();

    boolean existsByCpf(String cpf);

    long countByActiveTrue();

    long countByActiveTrueAndChurch_Id(UUID churchId);

    @Query("SELECT t FROM Tither t WHERE t.active = true AND " +
           "(:churchId IS NULL OR t.church.id = :churchId) AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR t.cpf LIKE CONCAT('%', :q, '%')) " +
           "ORDER BY t.name ASC")
    List<Tither> searchAll(String q, UUID churchId);
}
