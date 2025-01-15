package net.dmitrykornilov.pets.service;

import java.util.Collection;

import net.dmitrykornilov.pets.model.Owner;
import net.dmitrykornilov.pets.model.Pet;
import net.dmitrykornilov.pets.model.PetType;
import org.springframework.dao.DataAccessException;

public interface PetsService {
	Pet findPetById(int id) throws DataAccessException;
	Collection<Pet> findAllPets() throws DataAccessException;
	void savePet(Pet pet) throws DataAccessException;
	void deletePet(Pet pet) throws DataAccessException;

	Owner findOwnerById(int id) throws DataAccessException;
	Collection<Owner> findAllOwners() throws DataAccessException;
	void saveOwner(Owner owner) throws DataAccessException;
	void deleteOwner(Owner owner) throws DataAccessException;
	Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;

	PetType findPetTypeById(int petTypeId);
	Collection<PetType> findAllPetTypes() throws DataAccessException;
	Collection<PetType> findPetTypes() throws DataAccessException;
	void savePetType(PetType petType) throws DataAccessException;
	void deletePetType(PetType petType) throws DataAccessException;
    PetType findPetTypeByName(String name) throws DataAccessException;
}
