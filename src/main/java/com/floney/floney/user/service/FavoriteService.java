package com.floney.floney.user.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import com.floney.floney.common.exception.favorite.FavoriteAlreadyRegisteredException;
import com.floney.floney.common.exception.favorite.FavoriteNotFoundException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.user.dto.response.MyFavoriteResponseByFlow;
import com.floney.floney.user.entity.Favorite;
import com.floney.floney.user.repository.FavoriteRepository;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookLineRepository bookLineRepository;

    public void register(final String email, final Long bookLineId) {
        User user = getUser(email);
        BookLine bookLine = bookLineRepository.findByIdAndStatus(bookLineId, Status.ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);
        Optional<Favorite> favorite = favoriteRepository.findByUserAndBookLine(user, bookLine);
        if (favorite.isPresent() && favorite.get().isActive()) {
            throw new FavoriteAlreadyRegisteredException();
        }
        if (favorite.isPresent() && !favorite.get().isActive()) {
            favorite.get().activate();
            return;
        }
        Favorite forRegister = Favorite.of(user, bookLine);
        favoriteRepository.save(forRegister);
    }

    public void cancel(final String email, final Long bookLineId) {
        User user = getUser(email);
        BookLine bookLine = bookLineRepository.findByIdAndStatus(bookLineId, Status.ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);
        Optional<Favorite> forDelete = favoriteRepository.findByUserAndBookLine(user, bookLine);
        if (forDelete.isEmpty() || !forDelete.get().isActive()) {
            throw new FavoriteNotFoundException();
        }
        forDelete.get().inactive();
    }

    @Transactional(readOnly = true)
    public List<MyFavoriteResponseByFlow> showMyFavoritesByFlow(final String email, final CategoryType flowType) {
        User user = getUser(email);
        Category flow = categoryRepository.findByType(flowType)
            .orElseThrow(() -> new NotFoundCategoryException(flowType.name()));
        List<BookLine> bookLines = favoriteRepository.findAllBookLinesByUserAndFlow(user, flow);
        return bookLines.stream()
            .map(MyFavoriteResponseByFlow::from)
            .toList();
    }

    private User getUser(final String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
    }

}
