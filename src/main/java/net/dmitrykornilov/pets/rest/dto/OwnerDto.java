package net.dmitrykornilov.pets.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OwnerDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;

    @Valid
    private List<PetDto> pets = new ArrayList<>();

    public OwnerDto() {
        super();
    }

    public OwnerDto(Integer id,
                    String firstName,
                    String lastName,
                    String address,
                    String city,
                    String telephone,
                    List<PetDto> pets) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.pets = pets;
    }

    @Min(0)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]*$")
    @Size(min = 1, max = 30)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]*$")
    @Size(min = 1, max = 30)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Size(min = 1, max = 255)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NotNull
    @Size(min = 1, max = 80)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @NotNull
    @Pattern(regexp = "^[0-9]*$")
    @Size(min = 1, max = 20)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Valid
    public List<PetDto> getPets() {
        return pets;
    }

    public void setPets(List<PetDto> pets) {
        this.pets = pets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OwnerDto ownerDto = (OwnerDto) o;
        return Objects.equals(id, ownerDto.id)
                && Objects.equals(firstName, ownerDto.firstName)
                && Objects.equals(lastName, ownerDto.lastName)
                && Objects.equals(address, ownerDto.address)
                && Objects.equals(city, ownerDto.city)
                && Objects.equals(telephone, ownerDto.telephone)
                && Objects.equals(pets, ownerDto.pets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(firstName);
        result = 31 * result + Objects.hashCode(lastName);
        result = 31 * result + Objects.hashCode(address);
        result = 31 * result + Objects.hashCode(city);
        result = 31 * result + Objects.hashCode(telephone);
        result = 31 * result + Objects.hashCode(pets);
        return result;
    }
}
