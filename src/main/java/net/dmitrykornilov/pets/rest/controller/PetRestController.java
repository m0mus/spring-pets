package net.dmitrykornilov.pets.rest.controller;

import java.util.ArrayList;
import java.util.List;

import net.dmitrykornilov.pets.mapper.PetMapper;
import net.dmitrykornilov.pets.model.Pet;
import net.dmitrykornilov.pets.rest.dto.PetDto;
import net.dmitrykornilov.pets.service.PetsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class PetRestController {
    private final PetsService petsService;

    private final PetMapper petMapper;

    public PetRestController(PetsService petsService, PetMapper petMapper) {
        this.petsService = petsService;
        this.petMapper = petMapper;
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/pets/{petId}",
                    produces = { "application/json" })
    public ResponseEntity<PetDto> getPet(@Min(0) @PathVariable("petId") Integer petId) {
        PetDto pet = petMapper.toPetDto(this.petsService.findPetById(petId));
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/pets",
                    produces = { "application/json" })
    public ResponseEntity<List<PetDto>> listPets() {
        List<PetDto> pets = new ArrayList<>(petMapper.toPetsDto(this.petsService.findAllPets()));
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
                    value = "/pets/{petId}",
                    produces = { "application/json" },
                    consumes = { "application/json" })
    public ResponseEntity<PetDto> updatePet(@Min(0) @PathVariable("petId") Integer petId,
                                            @Valid @RequestBody PetDto petDto) {
        Pet currentPet = this.petsService.findPetById(petId);
        if (currentPet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPet.setBirthDate(petDto.getBirthDate());
        currentPet.setName(petDto.getName());
        currentPet.setType(petMapper.toPetType(petDto.getType()));
        this.petsService.savePet(currentPet);
        return new ResponseEntity<>(petMapper.toPetDto(currentPet), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/pets/{petId}",
                    produces = { "application/json" })
    public ResponseEntity<PetDto> deletePet(@Min(0) @PathVariable("petId") Integer petId) {
        Pet pet = this.petsService.findPetById(petId);
        if (pet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.petsService.deletePet(pet);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/pets",
                    produces = { "application/json" },
                    consumes = { "application/json" })
    public ResponseEntity<PetDto> addPet(@Valid @RequestBody PetDto petDto) {
        this.petsService.savePet(petMapper.toPet(petDto));
        return new ResponseEntity<>(petDto, HttpStatus.OK);
    }
}
