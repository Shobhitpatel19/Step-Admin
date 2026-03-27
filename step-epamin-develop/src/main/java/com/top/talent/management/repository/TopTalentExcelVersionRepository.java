package com.top.talent.management.repository;


import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface TopTalentExcelVersionRepository extends JpaRepository<TopTalentExcelVersion, Long> {

    Optional<TopTalentExcelVersion> findByVersionNameAndUploadedYear(String versionName, String year);

    List<TopTalentExcelVersion> findAllByUploadedYear(String year);

    boolean existsByVersionName(String versionName);



    boolean existsByFileName(String fileName);

    Optional<TopTalentExcelVersion> findByFileName(String fileName);

    ;

}

