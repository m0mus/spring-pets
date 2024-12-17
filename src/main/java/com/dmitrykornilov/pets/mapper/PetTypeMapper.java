package com.dmitrykornilov.pets.mapper;

import java.util.Collection;
import java.util.List;

import com.dmitrykornilov.pets.model.PetType;
import com.dmitrykornilov.pets.rest.dto.PetTypeDto;
import org.mapstruct.Mapper;

@Mapper
public interface PetTypeMapper {

    PetType toPetType(PetTypeDto petTypeDto);

    PetTypeDto toPetTypeDto(PetType petType);

    List<PetTypeDto> toPetTypeDtos(Collection<PetType> petTypes);
}
