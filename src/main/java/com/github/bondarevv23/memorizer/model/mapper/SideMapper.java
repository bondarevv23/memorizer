package com.github.bondarevv23.memorizer.model.mapper;

import com.github.bondarevv23.memorizer.model.Side;
import com.github.bondarevv23.memorizer.model.SideDTO;
import org.mapstruct.Mapper;

public interface SideMapper {
    Side sideDTOtoSide(SideDTO dto);
}
