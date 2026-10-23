package com.pcconfigurations.backend.repository;

import com.pcconfigurations.backend.entity.PcBuild;
import com.pcconfigurations.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PcBuildRepository extends JpaRepository<PcBuild, Long> {
    List<PcBuild> findByUserOrderByCreatedAtDesc(User user);
}
