package xyz.bookself.books.repository;

import org.springframework.data.repository.CrudRepository;
import xyz.bookself.books.domain.Rating;

public interface RatingRepository extends CrudRepository<Rating, Integer> { }
