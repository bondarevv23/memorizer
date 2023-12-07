package com.github.bondarevv23.memorizer.repository.jpa;

import com.github.bondarevv23.memorizer.model.Side;
import com.github.bondarevv23.memorizer.repository.interfaces.SideRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

public interface SideRepositoryJpa extends JpaRepository<Side, Integer>, SideRepository {
}
