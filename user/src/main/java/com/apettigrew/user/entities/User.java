package com.apettigrew.user.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Types;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.CHAR)
    private UUID uuid;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="username", nullable = false)
    private String username;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="role", nullable = false)
    private String role;

//    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
//    Set<Authority> authorities;

//    @OneToMany(mappedBy ="user", targetEntity = Invoice.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Invoice> invoices;

    @Column(name="enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name="birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name="created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

}
