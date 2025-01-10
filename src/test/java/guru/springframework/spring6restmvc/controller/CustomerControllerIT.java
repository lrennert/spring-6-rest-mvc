package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository  customerRepository;

    @Rollback
    @Transactional
    @Test
    void testListAllCustomers_emptyList() {
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.listAllCustomers();
        assertThat(dtos).isEmpty();
    }

    @Test
    void testListAllCustomers_success() {
        List<CustomerDTO> dtos = customerController.listAllCustomers();
        assertThat(dtos.size()).isEqualTo(3);
    }

    @Test
    void testGetCustomerById_notFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }

    @Test
    void testGetCustomerById_success() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDTO dto = customerController.getCustomerById(customer.getId());
        assertThat(dto).isNotNull();
    }
}