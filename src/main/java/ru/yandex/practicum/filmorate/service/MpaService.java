package ru.yandex.practicum.filmorate.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

@Service
public class MpaService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Set<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(Integer mpaId) {
        return mpaStorage.findById(mpaId);
    }
}
