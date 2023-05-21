package ru.yandex.practicum.filmorate.controller.storagecontrollers.varimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.storagecontrollers.VariableStorageController;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.DirectorRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.DirectorRestView;
import ru.yandex.practicum.filmorate.model.service.Director;
import ru.yandex.practicum.filmorate.service.CrudService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorStorageControllerImpl implements VariableStorageController<DirectorRestCommand, DirectorRestView> {
    @Qualifier("userService")
    private final CrudService<Director, DirectorRestCommand> directorService;
    private final DirectorMapper directorMapper;

    @Override
    @GetMapping
    public List<DirectorRestView> getAll() {
        log.debug("Запрошен список всех режиссеров. Количество сохраненных режиссеров: {}",
                directorService.getQuantity());
        return directorService.getAll().stream()
                .map(directorMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{director_id}")
    public DirectorRestView getOneById(@PathVariable(value = "director_id") @Positive long directorId) {
        Director director = directorService.getById((int) directorId);
        log.debug("Запрошен режиссер с идентификатором {}. Пользователь найден и отправлен клиенту", director.getId());
        return directorMapper.toRestView(director);
    }

    @PostMapping
    public DirectorRestView post(@RequestBody @Valid DirectorRestCommand postDirectorCommand) {
        Director director = directorService.save(postDirectorCommand);
        log.debug("Сохранен новый режиссер '{}'. Присвоен идентификатор {}",
                director.getName(), director.getId());
        return directorMapper.toRestView(director);
    }

    @PutMapping
    public DirectorRestView put(@RequestBody DirectorRestCommand putDirectorCommand) {
        Director director = directorService.update(putDirectorCommand);
        log.debug("Обновлены данные режиссера '{}'. Идентификатор режиссера: {}", director.getName(),
                director.getId());
        return directorMapper.toRestView(director);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех режиссеров из хранилища");
        directorService.deleteAll();
    }

    @Override
    @DeleteMapping("{director_id}")
    public DirectorRestView deleteOneById(@PathVariable(value = "director_id") @Positive long directorId) {
        Director director = directorService.deleteById((int) directorId);
        log.debug("Запрошено удаление режиссера '{}'. Режиссер удален", director.getName());
        return directorMapper.toRestView(director);
    }

}