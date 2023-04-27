package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookLineResponse;
import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.service.CategoryEnum.*;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {

    private final BookUserRepository bookUserRepository;

    private final BookLineRepository bookLineRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookLineResponse addBookLine(String auth, CreateLineRequest request) {
        BookLine bookLine = request.to(findBookUser(auth, request));
        bookLine.addCategory(findCategories(request));
        return BookLineResponse.of(bookLineRepository.save(bookLine));
    }

    private Map<CategoryEnum,Category> findCategories(CreateLineRequest request) {
        Map<CategoryEnum,Category> categories = new EnumMap<>(CategoryEnum.class);

        Category flow = findRoot(request.getFlow());

        Category asset = categoryRepository.findByNameAndParent(request.getAsset(), findRoot(ASSET.getRootName()))
            .orElseThrow(NotFoundCategoryException::new);

        Category flowLine = categoryRepository.findByNameAndParent(request.getLine(), flow)
            .orElseThrow(NotFoundCategoryException::new);

        categories.put(FLOW,flow);
        categories.put(ASSET,asset);
        categories.put(FLOW_LINE,flowLine);

        return categories;
    }
    private Category findRoot(String root) {
        return categoryRepository.findRoot(root);
    }
    private BookUser findBookUser(String auth, CreateLineRequest request) {
        return bookUserRepository.findUserWith(auth, request.getBookKey());
    }

}
