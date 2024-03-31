package com.floney.floney.favorite.service;

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
import com.floney.floney.favorite.dto.MyFavoriteResponseByFlow;
import com.floney.floney.favorite.entity.Favorite;
import com.floney.floney.favorite.repository.FavoriteRepository;
import com.floney.floney.user.dto.security.CustomUserDetails;
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

    public void register(CustomUserDetails userDetails, Long bookLineId) {
        User user = findUserByUserDetails(userDetails);
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

    public void cancel(CustomUserDetails userDetails, Long bookLineId) {
        User user = findUserByUserDetails(userDetails);
        BookLine bookLine = bookLineRepository.findByIdAndStatus(bookLineId, Status.ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);
        Optional<Favorite> forDelete = favoriteRepository.findByUserAndBookLine(user, bookLine);
        if (forDelete.isEmpty() || !forDelete.get().isActive()) {
            throw new FavoriteNotFoundException();
        }
        forDelete.get().inactive();
    }

    @Transactional(readOnly = true)
    public List<MyFavoriteResponseByFlow> showMyFavoritesByCategory(CustomUserDetails userDetails, CategoryType flowType) {
        User user = findUserByUserDetails(userDetails);
        Category flow = categoryRepository.findByType(flowType)
            .orElseThrow(() -> new NotFoundCategoryException(flowType.name()));
        List<BookLine> bookLines = favoriteRepository.findAllBookLinesByUserAndFlow(user, flow);
        return bookLines.stream()
            .map(MyFavoriteResponseByFlow::from)
            .toList();
    }

    private User findUserByUserDetails(CustomUserDetails userDetails) {
        User requestedUser = userDetails.getUser();
        return userRepository.findById(requestedUser.getId())
            .orElseThrow( () -> new UserNotFoundException(requestedUser.getEmail()) );
    }

}
