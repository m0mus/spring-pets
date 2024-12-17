package com.dmitrykornilov.pets.mapper;

import java.util.Collection;

import com.dmitrykornilov.pets.model.Pet;
import com.dmitrykornilov.pets.model.PetType;
import com.dmitrykornilov.pets.rest.dto.PetDto;
import com.dmitrykornilov.pets.rest.dto.PetTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    PetDto toPetDto(Pet pet);

    Collection<PetDto> toPetsDto(Collection<Pet> pets);

    Collection<Pet> toPets(Collection<PetDto> pets);

    Pet toPet(PetDto petDto);

    PetTypeDto toPetTypeDto(PetType petType);

    PetType toPetType(PetTypeDto petTypeDto);

    Collection<PetTypeDto> toPetTypeDtos(Collection<PetType> petTypes);
}
