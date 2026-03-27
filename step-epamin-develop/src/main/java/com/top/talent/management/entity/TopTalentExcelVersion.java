package com.top.talent.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity(name = "step_top_talent_excel_versions")
@Audited
public class TopTalentExcelVersion extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 10L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String versionName;

    private String uploadedYear;

    public TopTalentExcelVersion(String fileName, String versionName,LocalDateTime created,String createdBy,
                                 LocalDateTime lastUpdated,String lastUpdatedBy,String uploadedYear) {
        this.fileName = fileName;
        this.versionName = versionName;
        this.setCreated(created);
        this.setCreatedBy(createdBy);
        this.setLastUpdated(lastUpdated);
        this.setLastUpdatedBy(lastUpdatedBy);
        this.uploadedYear = uploadedYear;

    }

}
