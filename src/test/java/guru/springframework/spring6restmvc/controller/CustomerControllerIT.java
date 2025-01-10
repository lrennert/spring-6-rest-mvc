package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CustomerControllerIT {

	@Autowired
	CustomerController customerController;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerMapper customerMapper;

	@Test
	void testPatchCustomerById_notFound() {
		// given
		CustomerDTO dto = CustomerDTO.builder().build();

		// when, then
		assertThrows(NotFoundException.class, () -> customerController.patchCustomerById(UUID.randomUUID(), dto));
	}

	@Test
	void testPatchCustomerById_success() {
		// given
		CustomerDTO dto = CustomerDTO.builder()
				.name("patched name")
				.build();
		Customer customer = getAnyCustomer();

		// when
		ResponseEntity<?> responseEntity = customerController.patchCustomerById(customer.getId(), dto);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
		assertThat(customer.getName()).isEqualTo("patched name");
	}

	@Test
	void testDeleteCustomerById_notFound() {
		assertThrows(NotFoundException.class, () -> customerController.deleteCustomerById(UUID.randomUUID()));
	}

	@Test
	void testDeleteCustomerById_success() {
		// given
		Customer customer = getAnyCustomer();

		// when
		ResponseEntity<?> responseEntity = customerController.deleteCustomerById(customer.getId());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(customerRepository.findById(customer.getId()).isPresent()).isFalse();
		assertThat(customerRepository.findAll()).hasSize(2);
	}

	@Test
	void testUpdateCustomerById_notFound() {
		// given
		CustomerDTO dto = CustomerDTO.builder().build();

		// when, then
		assertThrows(NotFoundException.class, () -> customerController.updateCustomerByID(UUID.randomUUID(), dto));
	}

	@Test
	void testUpdateCustomerById_success() {
		// given
		Customer customer = getAnyCustomer();
		CustomerDTO dto = customerMapper.customerToCustomerDto(customer);
		dto.setName("updated name");

		// when
		ResponseEntity<?> responseEntity = customerController.updateCustomerByID(customer.getId(), dto);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
		assertThat(customerRepository.findById(customer.getId()).orElseThrow().getName()).isEqualTo("updated name");
	}

	@Test
	void testHandlePost() {
		// given
		CustomerDTO dto = CustomerDTO.builder()
				.name("Customer 4")
				.build();

		// when
		ResponseEntity<?> responseEntity = customerController.handlePost(dto);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
		assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

		String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
		UUID uuid = UUID.fromString(locationUUID[4]);
		Customer customer = customerRepository.findById(uuid).orElseThrow();
		assertThat(customer).isNotNull();
	}

	@Test
	void testListAllCustomers_emptyList() {
		// given
		customerRepository.deleteAll();

		// when
		List<CustomerDTO> dtos = customerController.listAllCustomers();

		// then
		assertThat(dtos.size()).isEqualTo(0);
	}

	@Test
	void testListAllCustomers_success() {
		// when
		List<CustomerDTO> dtos = customerController.listAllCustomers();

		// then
		assertThat(dtos.size()).isEqualTo(3);
	}

	@Test
	void testGetById_notFound() {
		// when, then
		assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
	}

	@Test
	void testGetById_success() {
		// given
		Customer customer = getAnyCustomer();

		// when
		CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());

		// then
		assertThat(customerDTO).isNotNull();
	}

	private Customer getAnyCustomer() {
		return customerRepository.findAll().getFirst();
	}
}