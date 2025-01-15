package net.dmitrykornilov.pets.service;

import java.util.Collection;

import net.dmitrykornilov.pets.model.Owner;
import net.dmitrykornilov.pets.model.Pet;
import net.dmitrykornilov.pets.model.PetType;
import net.dmitrykornilov.pets.repository.OwnerRepository;
import net.dmitrykornilov.pets.repository.PetRepository;
import net.dmitrykornilov.pets.repository.PetTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetsServiceImpl implements PetsService {
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
	private final PetTypeRepository petTypeRepository;

    @Autowired
	public PetsServiceImpl(
       		 PetRepository petRepository,
    		 OwnerRepository ownerRepository,
			 PetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
    }

	@Override
	@Transactional(readOnly = true)
	public Collection<Pet> findAllPets() throws DataAccessException {
		return petRepository.findAll();
	}

	@Override
	@Transactional
	public void deletePet(Pet pet) throws DataAccessException {
		petRepository.delete(pet);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<Owner> findAllOwners() throws DataAccessException {
		return ownerRepository.findAll();
	}

	@Override
	@Transactional
	public void deleteOwner(Owner owner) throws DataAccessException {
		ownerRepository.delete(owner);
	}

	@Override
    @Transactional(readOnly = true)
	public PetType findPetTypeById(int petTypeId) {
		PetType petType = null;
		try {
			petType = petTypeRepository.findById(petTypeId);
		} catch (ObjectRetrievalFailureException|EmptyResultDataAccessException e) {
		// just ignore not found exceptions for Jdbc/Jpa realization
			return null;
		}
		return petType;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<PetType> findAllPetTypes() throws DataAccessException {
		return petTypeRepository.findAll();
	}

	@Override
	@Transactional
	public void savePetType(PetType petType) throws DataAccessException {
		petTypeRepository.save(petType);
	}

	@Override
	@Transactional
	public void deletePetType(PetType petType) throws DataAccessException {
		petTypeRepository.delete(petType);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() throws DataAccessException {
		return petRepository.findPetTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public Owner findOwnerById(int id) throws DataAccessException {
		Owner owner = null;
		try {
			owner = ownerRepository.findById(id);
		} catch (ObjectRetrievalFailureException|EmptyResultDataAccessException e) {
		// just ignore not found exceptions for Jdbc/Jpa realization
			return null;
		}
		return owner;
	}

	@Override
	@Transactional(readOnly = true)
	public Pet findPetById(int id) throws DataAccessException {
		Pet pet = null;
		try {
			pet = petRepository.findById(id);
		} catch (ObjectRetrievalFailureException|EmptyResultDataAccessException e) {
		// just ignore not found exceptions for Jdbc/Jpa realization
			return null;
		}
		return pet;
	}

	@Override
	@Transactional
	public void savePet(Pet pet) throws DataAccessException {
		petRepository.save(pet);
	}

	@Override
	@Transactional
	public void saveOwner(Owner owner) throws DataAccessException {
		ownerRepository.save(owner);

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException {
		return ownerRepository.findByLastName(lastName);
	}

    @Override
    @Transactional(readOnly = true)
    public PetType findPetTypeByName(String name){
        PetType petType;
        try {
            petType = petTypeRepository.findByName(name);
        } catch (ObjectRetrievalFailureException|EmptyResultDataAccessException e) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
        return petType;
    }
}
