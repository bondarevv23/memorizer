package com.github.bondarevv23.memorizer.server.model.mapper;

import com.github.bondarevv23.memorizer.server.model.generated.*;
import com.github.bondarevv23.memorizer.server.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SideMapper {
    Side sideDTOtoSide(SideDTO dto);
}
