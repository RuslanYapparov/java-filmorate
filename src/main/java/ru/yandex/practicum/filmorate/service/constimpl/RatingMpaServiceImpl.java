package ru.yandex.practicum.filmorate.service.constimpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.service.RatingMpa;
import ru.yandex.practicum.filmorate.service.ReadConstantObjectService;

@Service
@Qualifier("ratingService")
public class RatingMpaServiceImpl implements ReadConstantObjectService<RatingMpa> {

    @Override
    public int getQuantity() {
        return RatingMpa.values().length;
    }

    @Override
    public RatingMpa getById(long id) throws ObjectNotFoundInStorageException {
        return RatingMpa.getRatingById((int) id);
    }

    @Override
    public List<RatingMpa> getAll() {
        return Arrays.stream(RatingMpa.values()).collect(Collectors.toList());
    }

}