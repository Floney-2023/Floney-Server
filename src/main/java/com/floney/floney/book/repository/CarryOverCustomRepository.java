package com.floney.floney.book.repository;

import com.floney.floney.book.entity.CarryOver;

import java.util.List;

public interface CarryOverCustomRepository {

    void deleteAllCarryOver(String bookKey);

    List<CarryOver> findCarryOverHaveToDelete();


}
