package com.top.talent.management.entity;

import com.top.talent.management.security.CustomUserPrincipal;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @LastModifiedBy
    @Column(nullable = false)
    private String lastUpdatedBy;

    public void setCreatedByAndUpdatedBy(CustomUserPrincipal customUserPrincipal){
        created = LocalDateTime.now();
        createdBy = customUserPrincipal.getFullName();
        lastUpdated = LocalDateTime.now();
        lastUpdatedBy = customUserPrincipal.getFullName();
    }

    public void setCreatedByAndUpdatedBy(String user){
        created = LocalDateTime.now();
        createdBy = user;
        lastUpdated = LocalDateTime.now();
        lastUpdatedBy = user;
    }

    public void setUpdatedBy(CustomUserPrincipal customUserPrincipal){
        lastUpdated = LocalDateTime.now();
        lastUpdatedBy = customUserPrincipal.getFullName();
    }

}
