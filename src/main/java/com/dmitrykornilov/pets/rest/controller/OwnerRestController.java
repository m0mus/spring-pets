package com.dmitrykornilov.pets.rest.controller;

import java.util.Collection;
import java.util.List;

import com.dmitrykornilov.pets.mapper.OwnerMapper;
import com.dmitrykornilov.pets.mapper.PetMapper;
import com.dmitrykornilov.pets.model.Owner;
import com.dmitrykornilov.pets.model.Pet;
import com.dmitrykornilov.pets.rest.dto.OwnerDto;
import com.dmitrykornilov.pets.rest.dto.PetDto;
import com.dmitrykornilov.pets.service.PetsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Validated
public class OwnerRestController {
    private final PetsService petsService;

    private final OwnerMapper ownerMapper;

    private final PetMapper petMapper;

    public OwnerRestController(PetsService petsService,
                               OwnerMapper ownerMapper,
                               PetMapper petMapper) {
        this.petsService = petsService;
        this.ownerMapper = ownerMapper;
        this.petMapper = petMapper;
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/owners",
                    produces = {"application/json"})
    public ResponseEntity<List<OwnerDto>> listOwners(@RequestParam(value = "lastName", required = false) String lastName) {
        Collection<Owner> owners;
        if (lastName != null) {
            owners = this.petsService.findOwnerByLastName(lastName);
        } else {
            owners = this.petsService.findAllOwners();
        }
        if (owners.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ownerMapper.toOwnerDtoCollection(owners), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/owners/{ownerId}",
                    produces = {"application/json"})
    public ResponseEntity<OwnerDto> getOwner(@Min(0) @PathVariable("ownerId") Integer ownerId) {
        Owner owner = this.petsService.findOwnerById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ownerMapper.toOwnerDto(owner), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/owners",
                    produces = {"application/json"},
                    consumes = {"application/json"})
    public ResponseEntity<OwnerDto> addOwner(@Valid @RequestBody OwnerDto ownerDto) {
        var headers = new HttpHeaders();

        ownerDto.setId(null);
        var owner = ownerMapper.toOwner(ownerDto);
        this.petsService.saveOwner(owner);

        var dto = ownerMapper.toOwnerDto(owner);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
        return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT,
                    value = "/owners/{ownerId}",
                    produces = {"application/json"},
                    consumes = {"application/json"})
    public ResponseEntity<OwnerDto> updateOwner(@Min(0) @PathVariable("ownerId") Integer ownerId,
                                                @Valid @RequestBody OwnerDto ownerDto) {
        Owner currentOwner = this.petsService.findOwnerById(ownerId);
        if (currentOwner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentOwner.setAddress(ownerDto.getAddress());
        currentOwner.setCity(ownerDto.getCity());
        currentOwner.setFirstName(ownerDto.getFirstName());
        currentOwner.setLastName(ownerDto.getLastName());
        currentOwner.setTelephone(ownerDto.getTelephone());
        this.petsService.saveOwner(currentOwner);
        return new ResponseEntity<>(ownerMapper.toOwnerDto(currentOwner), HttpStatus.NO_CONTENT);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/owners/{ownerId}",
                    produces = {"application/json"})
    public ResponseEntity<OwnerDto> deleteOwner(@Min(0) @PathVariable("ownerId") Integer ownerId) {
        Owner owner = this.petsService.findOwnerById(ownerId);
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.petsService.deleteOwner(owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/owners/{ownerId}/pets",
                    produces = {"application/json"},
                    consumes = {"application/json"})
    public ResponseEntity<PetDto> addPetToOwner(@Min(0) @PathVariable("ownerId") Integer ownerId,
                                                @Valid @RequestBody PetDto petDto) {
        var headers = new HttpHeaders();
        var pet = petMapper.toPet(petDto);

        var owner = petsService.findOwnerById(ownerId);
        pet.setOwner(owner);

        var petType = this.petsService.findPetTypeById(petDto.getType().getId());
        pet.setType(petType);

        this.petsService.savePet(pet);

        var dto = petMapper.toPetDto(pet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pets/{id}").buildAndExpand(pet.getId()).toUri());
        return new ResponseEntity<>(dto, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/owners/{ownerId}/pets/{petId}",
                    produces = {"application/json"})
    public ResponseEntity<PetDto> getOwnersPet(@Min(0) @PathVariable("ownerId") Integer ownerId,
                                               @Min(0) @PathVariable("petId") Integer petId) {
        Owner owner = this.petsService.findOwnerById(ownerId);
        Pet pet = this.petsService.findPetById(petId);
        if (owner == null || pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (!pet.getOwner().equals(owner)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.OK);
            }
        }
    }
}
