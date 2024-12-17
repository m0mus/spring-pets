package com.dmitrykornilov.pets.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PetDto {
    private Integer id;
    private Integer ownerId;
    private String name;
    private LocalDate birthDate;
    private PetTypeDto type;

    public PetDto() {
    }

    public PetDto(Integer id, Integer ownerId, String name, LocalDate birthDate, PetTypeDto type) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
    }

    @Min(0)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Min(0)
    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @NotNull
    @Size(max = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Valid
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @NotNull
    @Valid
    public PetTypeDto getType() {
        return type;
    }

    public void setType(PetTypeDto type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PetDto petDto = (PetDto) o;
        return Objects.equals(id, petDto.id)
                && Objects.equals(ownerId, petDto.ownerId)
                && Objects.equals(name, petDto.name)
                && Objects.equals(birthDate, petDto.birthDate)
                && Objects.equals(type, petDto.type);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(ownerId);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(birthDate);
        result = 31 * result + Objects.hashCode(type);
        return result;
    }
}

