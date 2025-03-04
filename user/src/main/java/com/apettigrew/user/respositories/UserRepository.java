package com.apettigrew.user.respositories;

import com.apettigrew.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.* FROM users u where u.uuid = :uuid AND u.deleted_at is NULL",
            nativeQuery = true)
    Optional<User> findByUuid(@Param("uuid") UUID uuid);

    @Query(value = "SELECT u.* FROM users u where u.username = :username AND u.deleted_at is NULL",
            nativeQuery = true)
    Optional<User> findByUserName(@Param("username") String username);
}