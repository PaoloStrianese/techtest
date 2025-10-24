package com.technicaltest.project_be.repository;

import com.technicaltest.project_be.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IReviewRepository extends JpaRepository<Review, String> {
}
