package com.github.bondarevv23.memorizer.server.repository.interfaces;

import com.github.bondarevv23.memorizer.server.model.Side;

import java.util.List;
import java.util.Optional;

public interface SideRepository {
    void deleteSideById(Integer id);

    Optional<Side> findSideById(Integer id);

    List<Side> findAll();

    Side save(Side side);
}
