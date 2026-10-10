package com.agent.engineering_agent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agent.engineering_agent.entity.CodeRepository;

@Repository
public interface CodeRepositoryRepository extends JpaRepository<CodeRepository, Long> {
    Optional<CodeRepository> findByRepoUrl(String repoUrl);
}
