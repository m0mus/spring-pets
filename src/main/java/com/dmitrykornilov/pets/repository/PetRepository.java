package com.dmitrykornilov.pets.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import com.dmitrykornilov.pets.model.BaseEntity;
import com.dmitrykornilov.pets.model.Pet;
import com.dmitrykornilov.pets.model.PetType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface PetRepository extends Repository<Pet, Integer> {
    @Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
    List<PetType> findPetTypes() throws DataAccessException;

    Pet findById(int id) throws DataAccessException;

    void save(Pet pet) throws DataAccessException;
    
	Collection<Pet> findAll() throws DataAccessException;

	void delete(Pet pet) throws DataAccessException;
}
