package com.floney.floney.book.repository.favorite;

import com.floney.floney.book.domain.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
