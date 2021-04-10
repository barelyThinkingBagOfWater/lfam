package ch.xavier.movies.repositories;

import ch.xavier.movies.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Long> {

    @Override
    Optional<Movie> findById(Long aLong);

    @Override
    <S extends Movie> List<S> saveAll(Iterable<S> entities);

    @Override
    <S extends Movie> S save(S s);

    @Override
    long count();
}
