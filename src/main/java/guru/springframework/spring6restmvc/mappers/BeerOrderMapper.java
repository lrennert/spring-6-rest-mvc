package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import org.mapstruct.Mapper;

@Mapper(uses = {BeerMapper.class, BeerOrderShipmentMapper.class, CustomerMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDTO beerOrderDTO);

    BeerOrderDTO beerOrderToBeerOrderDto(BeerOrder beerOrder);
}
