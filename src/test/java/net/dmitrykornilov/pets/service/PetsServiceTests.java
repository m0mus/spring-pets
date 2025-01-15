package net.dmitrykornilov.pets.service;

import java.time.LocalDate;
import java.util.Collection;

import net.dmitrykornilov.pets.model.BaseEntity;
import net.dmitrykornilov.pets.model.Owner;
import net.dmitrykornilov.pets.model.Pet;
import net.dmitrykornilov.pets.model.PetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PetsServiceTests {

    @Autowired
    protected PetsService petsService;

    @Test
    void shouldFindOwnersByLastName() {
        Collection<Owner> owners = this.petsService.findOwnerByLastName("Davis");
        assertThat(owners.size()).isEqualTo(2);

        owners = this.petsService.findOwnerByLastName("Daviss");
        assertThat(owners.isEmpty()).isTrue();
    }

    @Test
    void shouldFindSingleOwnerWithPet() {
        Owner owner = this.petsService.findOwnerById(1);
        assertThat(owner.getLastName()).startsWith("Franklin");
        assertThat(owner.getPets().size()).isEqualTo(1);
        assertThat(owner.getPets().get(0).getType()).isNotNull();
        assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
    }

    @Test
    @Transactional
    void shouldInsertOwner() {
        Collection<Owner> owners = this.petsService.findOwnerByLastName("Schultz");
        int found = owners.size();

        Owner owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Schultz");
        owner.setAddress("4, Evans Street");
        owner.setCity("Wollongong");
        owner.setTelephone("4444444444");
        this.petsService.saveOwner(owner);
        assertThat(owner.getId().longValue()).isNotEqualTo(0);
        assertThat(owner.getPet("null value")).isNull();
        owners = this.petsService.findOwnerByLastName("Schultz");
        assertThat(owners.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdateOwner() {
        Owner owner = this.petsService.findOwnerById(1);
        String oldLastName = owner.getLastName();
        String newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        this.petsService.saveOwner(owner);

        // retrieving new name from database
        owner = this.petsService.findOwnerById(1);
        assertThat(owner.getLastName()).isEqualTo(newLastName);
    }

    @Test
    void shouldFindPetWithCorrectId() {
        Pet pet7 = this.petsService.findPetById(7);
        assertThat(pet7.getName()).startsWith("Samantha");
        assertThat(pet7.getOwner().getFirstName()).isEqualTo("Jean");

    }

    @Test
    @Transactional
    void shouldInsertPetIntoDatabaseAndGenerateId() {
        Owner owner6 = this.petsService.findOwnerById(6);
        int found = owner6.getPets().size();

        Pet pet = new Pet();
        pet.setName("bowser");
        Collection<PetType> types = this.petsService.findPetTypes();
        pet.setType(getById(types, PetType.class, 2));
        pet.setBirthDate(LocalDate.now());
        owner6.addPet(pet);
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);

        this.petsService.savePet(pet);
        this.petsService.saveOwner(owner6);

        owner6 = this.petsService.findOwnerById(6);
        assertThat(owner6.getPets().size()).isEqualTo(found + 1);
        // checks that id has been generated
        assertThat(pet.getId()).isNotNull();
    }

    @Test
    @Transactional
    void shouldUpdatePetName() throws Exception {
        Pet pet7 = this.petsService.findPetById(7);
        String oldName = pet7.getName();

        String newName = oldName + "X";
        pet7.setName(newName);
        this.petsService.savePet(pet7);

        pet7 = this.petsService.findPetById(7);
        assertThat(pet7.getName()).isEqualTo(newName);
    }

    @Test
    void shouldFindAllPets(){
        Collection<Pet> pets = this.petsService.findAllPets();
        Pet pet1 = getById(pets, Pet.class, 1);
        assertThat(pet1.getName()).isEqualTo("Leo");
        Pet pet3 = getById(pets, Pet.class, 3);
        assertThat(pet3.getName()).isEqualTo("Rosy");
    }

    @Test
    @Transactional
    void shouldDeletePet(){
        Pet pet = this.petsService.findPetById(1);
        this.petsService.deletePet(pet);
        try {
            pet = this.petsService.findPetById(1);
		} catch (Exception e) {
			pet = null;
		}
        assertThat(pet).isNull();
    }

    @Test
    void shouldFindAllOwners(){
        Collection<Owner> owners = this.petsService.findAllOwners();
        Owner owner1 = getById(owners, Owner.class, 1);
        assertThat(owner1.getFirstName()).isEqualTo("George");
        Owner owner3 = getById(owners, Owner.class, 3);
        assertThat(owner3.getFirstName()).isEqualTo("Eduardo");
    }

    @Test
    @Transactional
    void shouldDeleteOwner(){
    	Owner owner = this.petsService.findOwnerById(1);
        this.petsService.deleteOwner(owner);
        try {
        	owner = this.petsService.findOwnerById(1);
		} catch (Exception e) {
			owner = null;
		}
        assertThat(owner).isNull();
    }

    @Test
    void shouldFindPetTypeById(){
    	PetType petType = this.petsService.findPetTypeById(1);
    	assertThat(petType.getName()).isEqualTo("cat");
    }

    @Test
    void shouldFindAllPetTypes(){
        Collection<PetType> petTypes = this.petsService.findAllPetTypes();
        PetType petType1 = getById(petTypes, PetType.class, 1);
        assertThat(petType1.getName()).isEqualTo("cat");
        PetType petType3 = getById(petTypes, PetType.class, 3);
        assertThat(petType3.getName()).isEqualTo("lizard");
    }

    @Test
    @Transactional
    void shouldInsertPetType() {
        Collection<PetType> petTypes = this.petsService.findAllPetTypes();
        int found = petTypes.size();

        PetType petType = new PetType();
        petType.setName("tiger");

        this.petsService.savePetType(petType);
        assertThat(petType.getId().longValue()).isNotEqualTo(0);

        petTypes = this.petsService.findAllPetTypes();
        assertThat(petTypes.size()).isEqualTo(found + 1);
    }

    @Test
    @Transactional
    void shouldUpdatePetType(){
    	PetType petType = this.petsService.findPetTypeById(1);
    	String oldLastName = petType.getName();
        String newLastName = oldLastName + "X";
        petType.setName(newLastName);
        this.petsService.savePetType(petType);
        petType = this.petsService.findPetTypeById(1);
        assertThat(petType.getName()).isEqualTo(newLastName);
    }

    @Test
    @Transactional
    void shouldDeletePetType(){
    	PetType petType = this.petsService.findPetTypeById(1);
        this.petsService.deletePetType(petType);
        try {
        	petType = this.petsService.findPetTypeById(1);
		} catch (Exception e) {
			petType = null;
		}
        assertThat(petType).isNull();
    }

    @Test
    @Transactional
    void shouldFindPetTypeByName(){
        PetType petType = this.petsService.findPetTypeByName("cat");
        assertThat(petType.getId()).isEqualTo(1);
    }

    private static <T extends BaseEntity> T getById(Collection<T> entities, Class<T> entityClass, int entityId)
            throws ObjectRetrievalFailureException {
        for (T entity : entities) {
            if (entity.getId() == entityId && entityClass.isInstance(entity)) {
                return entity;
            }
        }
        throw new ObjectRetrievalFailureException(entityClass, entityId);
    }
}
