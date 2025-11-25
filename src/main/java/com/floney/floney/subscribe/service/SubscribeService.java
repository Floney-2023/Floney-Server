package com.floney.floney.subscribe.service;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.favorite.Favorite;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.favorite.FavoriteRepository;
import com.floney.floney.book.util.CodeFactory;
import com.floney.floney.common.config.AwsService;
import com.floney.floney.common.dto.PresignedUrlDto;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.subscribe.Device;
import com.floney.floney.subscribe.dto.GetAndroidSubscribeInfoResponse;
import com.floney.floney.subscribe.dto.GetSubscribeInfoResponse;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.subscribe.dto.IsSubscribeBookResponse;
import com.floney.floney.subscribe.dto.IsSubscribeUserResponse;
import com.floney.floney.subscribe.entity.AppleSubscribe;
import com.floney.floney.subscribe.entity.AndroidSubscribe;
import com.floney.floney.subscribe.repository.AppleSubscribeRepository;
import com.floney.floney.subscribe.repository.AndroidSubscribeRepository;
import com.floney.floney.user.client.AndroidClient;
import com.floney.floney.user.client.AppleClient;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.floney.floney.book.domain.BookCapacity.DEFAULT;
import static com.floney.floney.book.domain.category.entity.Category.FAVORITE_MAX_SIZE;
import static com.floney.floney.book.service.BookServiceImpl.DEFAULT_BOOK_USER;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AppleClient appleClient;
    private final AndroidClient androidClient;
    private final AwsService awsService;
    private final FavoriteRepository favoriteRepository;
    private final BookUserRepository bookUserRepository;
    private final AppleSubscribeRepository appleSubscribeRepository;
    private final AndroidSubscribeRepository androidSubscribeRepository;

    public GetTransactionResponse isBookSubscribe(String bookKey) {
        final Book book = bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));

        String ownerEmail = book.getOwner();
        User owner = userRepository.findByEmailAndStatus(ownerEmail, ACTIVE)
            .orElseThrow(() -> new UsernameNotFoundException(ownerEmail));

        // TODO : owner device 저장
        if (this.appleClient.isSubscribe(owner).isValid()) {
            return this.appleClient.isSubscribe(owner);
        } else {
            return this.androidClient.isSubscribe(owner);
        }
    }

    public GetTransactionResponse isUserSubscribe(User user) {
        if (this.appleClient.isSubscribe(user).isValid()) {
            return this.appleClient.isSubscribe(user);
        } else {
            return this.androidClient.isSubscribe(user);
        }
    }

    public GetTransactionResponse isUserSubscribe(String email) {
        User user = this.userRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(() -> new NotFoundBookUserException(email, null));

        if (this.appleClient.isSubscribe(user).isValid()) {
            return this.appleClient.isSubscribe(user);
        } else {
            return this.androidClient.isSubscribe(user);
        }

    }

    public PresignedUrlDto getPresignedUrl(String bookKey) {

        String code = CodeFactory.generateCode();
        String fileName = bookKey + "/" + code;
        String url = this.awsService.generatePreSignedUrl(fileName);
        String viewUrl = "https://floney-images.s3.ap-northeast-2.amazonaws.com/" + fileName;
        return new PresignedUrlDto(fileName, url, viewUrl);
    }

    public IsSubscribeBookResponse isBenefitBook(String bookKey) {
        boolean maxFavorite = false;
        boolean overBookUser = false;
        Book book = bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE).orElseThrow(() -> new NotFoundBookException(bookKey));
        List<Favorite> favorite = this.favoriteRepository.findAllByBookAndStatus(book, ACTIVE);

        //1. 즐겨찾기 수 - 카테고리별 5개 제한 확인
        Map<Category, Long> favoriteCountByCategory = favorite.stream()
            .collect(Collectors.groupingBy(f -> f.getLineCategory(), Collectors.counting()));
        
        maxFavorite = favoriteCountByCategory.values().stream()
            .anyMatch(count -> count > FAVORITE_MAX_SIZE);

        // 2. 팀원 수
        List<OurBookUser> bookUsers = this.bookUserRepository.findAllUser(bookKey);
        if (bookUsers.size() > DEFAULT_BOOK_USER) {
            overBookUser = true;
        }

        return new IsSubscribeBookResponse(maxFavorite, overBookUser);
    }

    public IsSubscribeUserResponse isBenefitUser(User user) {
        boolean maxBook = false;
        int currentJoinBook = bookUserRepository.countBookUserByUserAndStatus(user, ACTIVE);

        if (currentJoinBook > DEFAULT.getValue()) {
            maxBook = true;
        }

        return new IsSubscribeUserResponse(maxBook);
    }

    public GetAndroidSubscribeInfoResponse getAndroidSubscribeInfo(User user) {
        GetAndroidSubscribeInfoResponse res = new GetAndroidSubscribeInfoResponse(this.androidClient.getAndroidSubscribe(user));
        
        // 현재 시간과 만료 시간 비교하여 구독 상태 설정
        long currentTimeMillis = System.currentTimeMillis();
        boolean isCurrentSubscribe = Long.parseLong(res.getExpiryTimeMillis()) >= currentTimeMillis;
        res.setCurrentSubscribe(isCurrentSubscribe);
        
        return res;
    }
    
    public GetSubscribeInfoResponse getSubscribeInfo(User user) {
        // iOS 구독 확인
        GetTransactionResponse appleSubscribe = this.appleClient.isSubscribe(user);
        if (appleSubscribe.isValid()) {
            Optional<AppleSubscribe> appleSubscription = this.appleSubscribeRepository.findFirstByUserAndStatusOrderByUpdatedAtDesc(user, ACTIVE);
            if (appleSubscription.isPresent()) {
                AppleSubscribe subscription = appleSubscription.get();
                return new GetSubscribeInfoResponse(true, subscription.getExpiresDate(), subscription.getCurrency(), null);
            }
        }
        
        // Android 구독 확인
        GetTransactionResponse androidSubscribe = this.androidClient.isSubscribe(user);
        if (androidSubscribe.isValid()) {
            GetAndroidSubscribeInfoResponse androidResponse = this.getAndroidSubscribeInfo(user);
            return new GetSubscribeInfoResponse(androidResponse);
        }
        
        // 구독이 없는 경우 기본값 반환
        return new GetSubscribeInfoResponse(false, null, null, null);
    }
    
    public void inactiveUserSubscription(User user) {
        // Apple 모든 구독 비활성화
        appleSubscribeRepository.findByUser(user)
            .forEach(subscription -> {
                subscription.inactive();
                appleSubscribeRepository.save(subscription);
            });
        
        // Android 모든 구독 비활성화
        androidSubscribeRepository.findByUser(user)
            .forEach(subscription -> {
                subscription.inactive();
                androidSubscribeRepository.save(subscription);
            });
    }
}
