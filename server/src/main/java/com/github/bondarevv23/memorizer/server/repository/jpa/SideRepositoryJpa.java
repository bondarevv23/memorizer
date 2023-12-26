package com.github.bondarevv23.memorizer.server.repository.jpa;

import com.github.bondarevv23.memorizer.server.model.Side;
import com.github.bondarevv23.memorizer.server.repository.interfaces.SideRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SideRepositoryJpa extends JpaRepository<Side, Integer>, SideRepository {
}
