package com.dmitrykornilov.pets.rest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dmitrykornilov.pets.mapper.PetTypeMapper;
import com.dmitrykornilov.pets.model.PetType;
import com.dmitrykornilov.pets.service.ApplicationTestConfig;
import com.dmitrykornilov.pets.service.PetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes=ApplicationTestConfig.class)
@WebAppConfiguration
class PetTypeRestControllerTests {
    @Autowired
    private PetTypeRestController petTypeRestController;

    @Autowired
    private PetTypeMapper petTypeMapper;

    @MockBean
    private PetsService petsService;

    private MockMvc mockMvc;

	private final ObjectMapper mapper = new ObjectMapper();

    private List<PetType> petTypes = new ArrayList<>();

    @BeforeEach
    void initPetTypes(){
    	this.mockMvc = MockMvcBuilders.standaloneSetup(petTypeRestController).build();

		petTypes.clear();

    	PetType petType = new PetType();
    	petType.setId(1);
    	petType.setName("cat");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(2);
    	petType.setName("dog");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(3);
    	petType.setName("lizard");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(4);
    	petType.setName("snake");
    	petTypes.add(petType);
    }

    @Test
    void testGetPetTypeSuccess() throws Exception {
    	given(this.petsService.findPetTypeById(1)).willReturn(petTypes.get(0));
        this.mockMvc.perform(get("/api/pettypes/1")
        	.accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("cat"));
    }

    @Test
    void testGetPetTypeNotFound() throws Exception {
    	given(this.petsService.findPetTypeById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/pettypes/999")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPetTypesSuccess() throws Exception {
    	petTypes.remove(0);
    	petTypes.remove(1);
    	given(this.petsService.findAllPetTypes()).willReturn(petTypes);
        this.mockMvc.perform(get("/api/pettypes/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
        	.andExpect(jsonPath("$.[0].id").value(2))
        	.andExpect(jsonPath("$.[0].name").value("dog"))
        	.andExpect(jsonPath("$.[1].id").value(4))
        	.andExpect(jsonPath("$.[1].name").value("snake"));
    }

    @Test
    void testGetAllPetTypesNotFound() throws Exception {
    	given(this.petsService.findAllPetTypes()).willReturn(Collections.emptyList());
        this.mockMvc.perform(get("/api/pettypes/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePetTypeSuccess() throws Exception {
		var petTypeDto = petTypeMapper.toPetTypeDto(petTypes.get(0));
		petTypeDto.setId(null);

		var petTypeJson = this.mapper.writeValueAsString(petTypeDto);
    	this.mockMvc.perform(post("/api/pettypes/")
    		.content(petTypeJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
    		.andExpect(status().isCreated());
    }

    @Test
    void testCreatePetTypeError() throws Exception {
    	var petTypeDto = petTypeMapper.toPetTypeDto(petTypes.get(0));
    	petTypeDto.setId(null);
    	petTypeDto.setName(null);

        var petTypeJson = this.mapper.writeValueAsString(petTypeDto);
    	this.mockMvc.perform(post("/api/pettypes/")
        		.content(petTypeJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        		.andExpect(status().isBadRequest());
     }
    @Test
    void testCreatePetTypeErrorWithId() throws Exception {
		var petTypeDto = petTypeMapper.toPetTypeDto(petTypes.get(1));
        var petTypeJson = this.mapper.writeValueAsString(petTypeDto);
        this.mockMvc.perform(post("/api/pettypes/")
                .content(petTypeJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
    @Test
    void testUpdatePetTypeSuccess() throws Exception {
		var petTypeDto = petTypeMapper.toPetTypeDto(petTypes.get(1));
		petTypeDto.setName("sobaka");
		var petTypeJson = this.mapper.writeValueAsString(petTypeDto);

		given(this.petsService.findPetTypeById(2)).willReturn(petTypes.get(1));

		this.mockMvc.perform(put("/api/pettypes/2")
    		.content(petTypeJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(content().contentType("application/json"))
        	.andExpect(status().isNoContent());

    	this.mockMvc.perform(get("/api/pettypes/2")
           	.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("sobaka"));
    }

    @Test
    void testUpdatePetTypeError() throws Exception {
    	var petTypeDto = petTypeMapper.toPetTypeDto(petTypes.get(0));
    	petTypeDto.setName("");

		var petTypeJson = mapper.writeValueAsString(petTypeDto);
    	this.mockMvc.perform(put("/api/pettypes/1")
    		.content(petTypeJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isBadRequest());
     }

    @Test
    void testDeletePetTypeSuccess() throws Exception {
    	given(this.petsService.findPetTypeById(1)).willReturn(petTypes.get(0));
    	this.mockMvc.perform(delete("/api/pettypes/1")
    		.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNoContent());
    }

    @Test
    void testDeletePetTypeError() throws Exception {
    	given(this.petsService.findPetTypeById(999)).willReturn(null);
    	this.mockMvc.perform(delete("/api/pettypes/999")
    		.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNotFound());
    }
}
