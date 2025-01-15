package net.dmitrykornilov.pets.repository;

import java.util.Collection;

import net.dmitrykornilov.pets.model.Owner;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface OwnerRepository extends Repository<Owner, Integer> {
    @Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
    Collection<Owner> findByLastName(@Param("lastName") String lastName);

    @Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
    Owner findById(@Param("id") int id);

    void save(Owner owner) throws DataAccessException;
    
	Collection<Owner> findAll() throws DataAccessException;
	
	void delete(Owner owner) throws DataAccessException;
}
