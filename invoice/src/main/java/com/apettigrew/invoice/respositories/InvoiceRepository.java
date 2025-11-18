package com.apettigrew.invoice.respositories;

import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Query("SELECT i FROM Invoice i WHERE i.deletedAt IS NULL")
    Page<Invoice> findAllActive(Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Invoice> findByIdActive(@Param("id") Integer id);

    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.deletedAt IS NULL")
    Page<Invoice> findByStatus(@Param("status") InvoiceStatus status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.userId = :userId AND i.deletedAt IS NULL")
    Page<Invoice> findAllActiveByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.userId = :userId AND i.status = :status AND i.deletedAt IS NULL")
    Page<Invoice> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") InvoiceStatus status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.id = :id AND i.userId = :userId AND i.deletedAt IS NULL")
    Optional<Invoice> findByIdAndUserId(@Param("id") Integer id, @Param("userId") String userId);
}