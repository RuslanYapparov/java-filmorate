package ru.yandex.practicum.filmorate.service.constimpl;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.service.Genre;
import ru.yandex.practicum.filmorate.service.ReadConstantObjectService;

@Service
public class GenreServiceImpl implements ReadConstantObjectService<Genre> {

    @Override
    public int getQuantity() {
        return Genre.values().length;
    }

    @Override
    public Genre getById(long id) throws ObjectNotFoundInStorageException {
        return Genre.getGenreById((int) id);
    }

    @Override
    public List<Genre> getAll() {
        return Arrays.stream(Genre.values()).collect(Collectors.toList());
    }

}