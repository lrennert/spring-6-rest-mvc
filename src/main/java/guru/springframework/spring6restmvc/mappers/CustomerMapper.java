package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "beerOrders", ignore = true)
    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDto(Customer customer);

}
