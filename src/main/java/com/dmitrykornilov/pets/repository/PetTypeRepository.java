package com.dmitrykornilov.pets.repository;

import java.util.Collection;

import com.dmitrykornilov.pets.model.PetType;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.Repository;

public interface PetTypeRepository extends Repository<PetType, Integer> {
	PetType findById(int id) throws DataAccessException;

    PetType findByName(String name) throws DataAccessException;

	Collection<PetType> findAll() throws DataAccessException;

	void save(PetType petType) throws DataAccessException;

	void delete(PetType petType) throws DataAccessException;
}
