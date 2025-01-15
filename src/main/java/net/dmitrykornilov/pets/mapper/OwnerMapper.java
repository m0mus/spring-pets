package net.dmitrykornilov.pets.mapper;

import java.util.Collection;
import java.util.List;

import net.dmitrykornilov.pets.model.Owner;
import net.dmitrykornilov.pets.rest.dto.OwnerDto;
import org.mapstruct.Mapper;

@Mapper(uses = PetMapper.class)
public interface OwnerMapper {

    OwnerDto toOwnerDto(Owner owner);

    Owner toOwner(OwnerDto ownerDto);

    List<OwnerDto> toOwnerDtoCollection(Collection<Owner> ownerCollection);

    Collection<Owner> toOwners(Collection<OwnerDto> ownerDtos);
}
