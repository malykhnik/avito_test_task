package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DecisionRepository extends JpaRepository<Decision, UUID> {
}
