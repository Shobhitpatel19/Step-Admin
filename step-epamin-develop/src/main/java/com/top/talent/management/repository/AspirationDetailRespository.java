package com.top.talent.management.repository;

import com.top.talent.management.entity.AspirationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AspirationDetailRespository extends JpaRepository<AspirationDetail, Long> {
   AspirationDetail findByTitle(String title);
   List<AspirationDetail> findByTitleIn(List<String> titles);
}
