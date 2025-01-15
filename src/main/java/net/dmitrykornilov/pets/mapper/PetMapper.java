package net.dmitrykornilov.pets.mapper;

import java.util.Collection;

import net.dmitrykornilov.pets.model.Pet;
import net.dmitrykornilov.pets.model.PetType;
import net.dmitrykornilov.pets.rest.dto.PetDto;
import net.dmitrykornilov.pets.rest.dto.PetTypeDto;
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
