package com.floney.floney.alarm.service;

import com.floney.floney.book.dto.request.SaveAlarmRequest;
import com.floney.floney.book.dto.request.UpdateAlarmReceived;
import com.floney.floney.book.domain.entity.Alarm;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.alarm.repository.AlarmRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.book.NotFoundAlarmException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.user.dto.response.AlarmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class BookAlarmServiceImpl implements BookAlarmService {
    private final AlarmRepository alarmRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;

    @Override
    @Transactional
    public void saveAlarm(SaveAlarmRequest request) {
        final String bookKey = request.getBookKey();
        final String userEmail = request.getUserEmail();

        final BookUser bookUser = bookUserRepository.findBookUserByEmailAndBookKey(userEmail, bookKey)
            .orElseThrow(() -> new NotFoundBookUserException(bookKey, userEmail));

        final Alarm alarm = Alarm.of(findBook(bookKey), bookUser, request);
        alarmRepository.save(alarm);
    }

    @Override
    @Transactional
    public void updateAlarmReceived(UpdateAlarmReceived request) {
        Alarm alarm = alarmRepository.findById(request.getId())
            .orElseThrow(() -> new NotFoundAlarmException(request.getId()));
        alarm.updateReceived(request.isReceived());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlarmResponse> getAlarmByBook(String bookKey, String email) {
        final BookUser bookUser = bookUserRepository.findBookUserByEmailAndBookKey(email, bookKey)
            .orElseThrow(() -> new NotFoundBookUserException(bookKey, email));

        return alarmRepository.findAllByBookAndBookUser(findBook(bookKey), bookUser)
            .stream()
            .map(AlarmResponse::of)
            .toList();
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }
}
