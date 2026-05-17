package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvc.model.BeerOrderLineCreateDTO;
import guru.springframework.spring6restmvc.model.BeerOrderLineUpdateDTO;
import guru.springframework.spring6restmvc.model.BeerOrderShipmentUpdateDTO;
import guru.springframework.spring6restmvc.model.BeerOrderUpdateDTO;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;
import java.util.stream.Collectors;

import static guru.springframework.spring6restmvc.controller.BeerControllerTest.JWT_REQUEST_POST_PROCESSOR;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerOrderControllerIT {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testListBeerOrders() throws Exception {
        mockMvc.perform(get(BeerOrderController.BEER_ORDERS_PATH)
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)));
    }

    @Test
    void testGetBeerOrderById() throws Exception {
        val beerOrder = beerOrderRepository.findAll().getFirst();

        mockMvc.perform(get(BeerOrderController.BEER_ORDERS_PATH_ID, beerOrder.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(beerOrder.getId().toString())));
    }

    @Test
    void testCreateBeerOrder() throws Exception {
        val customerId = customerRepository.findAll().getFirst().getId();
        val beerId = beerRepository.findAll().getFirst().getId();

        val beerOrderCreateDTO = BeerOrderCreateDTO.builder()
                .customerRef("123")
                .customerId(customerId)
                .beerOrderLines(Set.of(
                        BeerOrderLineCreateDTO.builder()
                                .beerId(beerId)
                                .orderQuantity(7)
                                .build()))
                .build();

        mockMvc.perform(post(BeerOrderController.BEER_ORDERS_PATH)
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Transactional
    @Test
    void testUpdateBeerOrder() throws Exception {
        val beerOrder = beerOrderRepository.findAll().getFirst();

        val beerOrderUpdateDTO = BeerOrderUpdateDTO.builder()
                .id(beerOrder.getId())
                .customerRef("updated")
                .customerId(beerOrder.getCustomer().getId())
                .beerOrderLines(beerOrder.getBeerOrderLines()
                        .stream()
                        .map(line -> BeerOrderLineUpdateDTO.builder()
                                .id(line.getId())
                                .beerId(line.getBeer().getId())
                                .orderQuantity(line.getOrderQuantity())
                                .quantityAllocated(line.getQuantityAllocated())
                                .build())
                        .collect(Collectors.toSet()))
                .beerOrderShipment(BeerOrderShipmentUpdateDTO.builder()
                        .trackingNumber("123456")
                        .build())
                .build();

        mockMvc.perform(put(BeerOrderController.BEER_ORDERS_PATH_ID, beerOrder.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerRef", is("updated")));
    }
}