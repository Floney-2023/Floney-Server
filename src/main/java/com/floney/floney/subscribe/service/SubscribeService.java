package com.floney.floney.subscribe.service;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.util.CodeFactory;
import com.floney.floney.common.config.AwsService;
import com.floney.floney.common.dto.PresignedUrlDto;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.subscribe.Device;
import com.floney.floney.subscribe.dto.GetTransactionResponse;
import com.floney.floney.user.client.AndroidClient;
import com.floney.floney.user.client.AppleClient;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AppleClient appleClient;
    private final AndroidClient androidClient;
    private final AwsService awsService;

    public GetTransactionResponse isBookSubscribe(String bookKey) {
        final Book book = bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));

        String ownerEmail = book.getOwner();
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new UsernameNotFoundException(ownerEmail));

        // TODO : owner device 저장
        if (this.appleClient.isSubscribe(owner).isValid()) {
            return this.appleClient.isSubscribe(owner);
        } else {
            return this.androidClient.isSubscribe(owner);
        }
    }

    public GetTransactionResponse isUserSubscribe(String device, User user) {
        if (device.equals(Device.ANDROID.value)) {
            return androidClient.isSubscribe(user);
        } else {
            return appleClient.isSubscribe(user);
        }
    }

    public PresignedUrlDto getPresignedUrl(String bookKey) {
        String code = CodeFactory.generateCode();
        String fileName = bookKey + "/" + code;
        String url = this.awsService.generatePreSignedUrl(fileName);
        return new PresignedUrlDto(fileName, url);
    }
}
