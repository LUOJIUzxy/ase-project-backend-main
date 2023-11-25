package com.tum.ase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class DeliveryServiceApplicationTests {
	
	@Test
	void contextLoads() {
	}
	/* 
	@BeforeEach
	void setUp() {
		//create sample db
		AppUser testDispatcher = new AppUser("dispatcher", "test1@gmail.com", null, UserRole.DISPATCHER);
		testDispatcher.setId("63d76a8b982f9143e56af4bb");
		AppUser testDeliverer = new AppUser("deliverer", "test2@gmail.com", "w9zap1br", UserRole.DELIVERER);
		testDeliverer.setId("63d76a8b982f9143e56af4bc");
		AppUser testCustomer = new AppUser("customer", "test3@gmail.com", "f784xjmc", UserRole.CUSTOMER);
		testCustomer.setId("63d76a8b982f9143e56af4bd");
		Box testBox = new Box("box", "Garching", "1234567890");
		testBox.setId("63d76a8b982f9143e56af4be");

		AssignUser ac = new AssignUser("customer", "test3@gmail.com", "f784xjmc", UserRole.CUSTOMER);
		AssignUser ad = new AssignUser("deliverer", "test2@gmail.com", "w9zap1br", UserRole.DELIVERER);
		AssignBox ab = new AssignBox("box", "Garching", "1234567890");
		Order o  = new Order(ac, ad, ab, "QRQRQRQR", "0");
		orderRepository.save(o);
	}

	@Test
	void testGetAllBoxes() throws Exception {
		List<Box> boxes = boxRepository.findAll();

		ResultActions request = mockMvc.perform(get("/box/list")).andDo(print()).andExpect(status().isOk());
		
		String jsonString = request.andReturn().getResponse().getContentAsString();
		List<Box> retBoxes = Arrays.stream(objectMapper.readValue(jsonString, Box[].class)).collect(Collectors.toList());

		assertThat(retBoxes).isEqualTo(boxes);
	}

	@Test
	void testGetBoxByIdExisting() throws Exception {
		List<Box> boxes = boxRepository.findAll();
		Box box = boxes.get(0);
		String id = box.getId();

		ResultActions request = mockMvc.perform(get("/box/list/" + id)).andDo(print()).andExpect(status().isOk());
		
		String jsonString = request.andReturn().getResponse().getContentAsString();
		
		assertThat(objectMapper.readValue(jsonString, Box.class)).isEqualTo(box);
	}

	@Test
	void testGetBoxByIdNotExisting() throws Exception {
		ResultActions request = mockMvc.perform(get("/box/list/999")).andDo(print()).andExpect(status().isNotFound());
		int status = request.andReturn().getResponse().getStatus();
		assertThat(status).isEqualTo(404);
	}

	@Test
	void testCreateBox() throws Exception {
		Box newBox = new Box("garching04", "XXXXXX,XXXXXXX,XXXXX", "XXXXXXXXXXXX");
		ResultActions request = mockMvc.perform( MockMvcRequestBuilders
							.post("/box/add")
							.content(asJsonString(newBox))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isOk())
							.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		
		String jsonString = request.andReturn().getResponse().getContentAsString();
		newBox = objectMapper.readValue(jsonString, Box.class);
		Box addedBox = boxRepository.findBoxById(newBox.getId());
		assertThat(objectMapper.readValue(jsonString, Box.class)).isEqualTo(addedBox);
	}

	void testUpdateBox() throws Exception {
		List<Box> boxes = boxRepository.findAll();
		Box box = boxes.get(0);
		String id = box.getId();
		Box updatedBox = new Box(box.getId(), "UUUUUU, UUU, UUUUU", "UUUUUUUUUU");
		ResultActions request = mockMvc.perform( MockMvcRequestBuilders
							.post("/box/update/" + id)
							.content(asJsonString(updatedBox))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON))
							.andExpect(status().isOk())
							.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
		
		String jsonString = request.andReturn().getResponse().getContentAsString();
		assertThat(objectMapper.readValue(jsonString, Box.class)).isEqualTo(updatedBox);
		
		ResultActions verifyRequest = mockMvc.perform(get("/box/list/" + id)).andDo(print()).andExpect(status().isOk());
		String result = verifyRequest.andReturn().getResponse().getContentAsString();
		assertThat(objectMapper.readValue(result, Box.class)).isEqualTo(updatedBox);
	}

	void testDeleteBox() throws Exception {
		List<Box> boxes = boxRepository.findAll();
		Box deletedBox = boxes.get(0);
		String id = deletedBox.getId();
		
		ResultActions request = mockMvc.perform(MockMvcRequestBuilders.delete("/box/delete/" + deletedBox.getId())).andDo(print()).andExpect(status().isOk());
		String jsonString = request.andReturn().getResponse().getContentAsString();
		assertThat(objectMapper.readValue(jsonString, Box.class)).isEqualTo(deletedBox);
		
		ResultActions verifyRequest = mockMvc.perform(get("/box/delete/" + deletedBox.getId())).andDo(print()).andExpect(status().isNotFound());
		int status = verifyRequest.andReturn().getResponse().getStatus();
		assertThat(status).isEqualTo(404);
	}
	*/
}
