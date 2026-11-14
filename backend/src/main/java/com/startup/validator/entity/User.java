package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA User Entity representing the 'users' table in the MySQL database.
 * 
 * Explanation of Annotations:
 * `@Entity`: Marks this class as a JPA entity mapped to a database table.
 * `@Table`: Specifies the name of the database table ('users') and adds unique constraints on username and email.
 * `@Data`: Lombok annotation to generate getters, setters, equals, hashCode, and toString methods.
 * `@Builder`: Lombok annotation that implements the Builder pattern for easy instantiation.
 * `@NoArgsConstructor` & `@AllArgsConstructor`: Lombok annotations to generate constructors.
 * `@Id`: Specifies the primary key of the entity.
 * `@GeneratedValue`: Configures the primary key generation strategy (Identity uses MySQL AUTO_INCREMENT).
 * `@Enumerated(EnumType.STRING)`: Stores the Role enum in database as a string (e.g. 'USER', 'ADMIN') instead of ordinal index.
 * `@CreationTimestamp` & `@UpdateTimestamp`: Hibernate annotations to automatically handle audit timestamps.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
