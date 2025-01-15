package net.dmitrykornilov.pets.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.dmitrykornilov.pets.mapper.PetTypeMapper;
import net.dmitrykornilov.pets.model.PetType;
import net.dmitrykornilov.pets.rest.dto.PetTypeDto;
import net.dmitrykornilov.pets.service.PetsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api")
public class PetTypeRestController {
    private final PetsService petsService;
    private final PetTypeMapper petTypeMapper;

    public PetTypeRestController(PetsService petsService, PetTypeMapper petTypeMapper) {
        this.petsService = petsService;
        this.petTypeMapper = petTypeMapper;
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/pettypes",
                    produces = {"application/json"})
    public ResponseEntity<List<PetTypeDto>> listPetTypes() {
        List<PetType> petTypes = new ArrayList<>(this.petsService.findAllPetTypes());
        if (petTypes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petTypeMapper.toPetTypeDtos(petTypes), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/pettypes/{petTypeId}",
                    produces = {"application/json"})
    public ResponseEntity<PetTypeDto> getPetType(@Min(0) @PathVariable("petTypeId") Integer petTypeId) {
        PetType petType = this.petsService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(petTypeMapper.toPetTypeDto(petType), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/pettypes",
                    produces = {"application/json"},
                    consumes = {"application/json"})
    public ResponseEntity<PetTypeDto> addPetType(@Valid @RequestBody PetTypeDto petTypeDto) {
        HttpHeaders headers = new HttpHeaders();
        if (Objects.nonNull(petTypeDto.getId()) && !petTypeDto.getId().equals(0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            final PetType type = petTypeMapper.toPetType(petTypeDto);
            this.petsService.savePetType(type);
            headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pettypes/{id}").buildAndExpand(type.getId())
                                        .toUri());
            return new ResponseEntity<>(petTypeMapper.toPetTypeDto(type), headers, HttpStatus.CREATED);
        }
    }

    @RequestMapping(method = RequestMethod.PUT,
                    value = "/pettypes/{petTypeId}",
                    produces = {"application/json"},
                    consumes = {"application/json"})
    public ResponseEntity<PetTypeDto> updatePetType(@Min(0) @PathVariable("petTypeId") Integer petTypeId,
                                                    @Valid @RequestBody PetTypeDto petTypeDto) {
        PetType currentPetType = this.petsService.findPetTypeById(petTypeId);
        if (currentPetType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPetType.setName(petTypeDto.getName());
        this.petsService.savePetType(currentPetType);
        return new ResponseEntity<>(petTypeMapper.toPetTypeDto(currentPetType), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/pettypes/{petTypeId}",
            produces = {"application/json"}
    )
    @Transactional
    public ResponseEntity<PetTypeDto> deletePetType(@Min(0) @PathVariable("petTypeId")Integer petTypeId) {
        PetType petType = this.petsService.findPetTypeById(petTypeId);
        if (petType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.petsService.deletePetType(petType);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
