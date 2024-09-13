package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Feedback;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Optional<List<Feedback>> findByUser(User user);
}
