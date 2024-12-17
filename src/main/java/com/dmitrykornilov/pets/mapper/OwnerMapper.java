package com.dmitrykornilov.pets.mapper;

import java.util.Collection;
import java.util.List;

import com.dmitrykornilov.pets.model.Owner;
import com.dmitrykornilov.pets.rest.dto.OwnerDto;
import org.mapstruct.Mapper;

@Mapper(uses = PetMapper.class)
public interface OwnerMapper {

    OwnerDto toOwnerDto(Owner owner);

    Owner toOwner(OwnerDto ownerDto);

    List<OwnerDto> toOwnerDtoCollection(Collection<Owner> ownerCollection);

    Collection<Owner> toOwners(Collection<OwnerDto> ownerDtos);
}
