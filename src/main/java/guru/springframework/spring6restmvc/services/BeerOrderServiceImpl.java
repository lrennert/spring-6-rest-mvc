package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.controller.NotFoundException;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import guru.springframework.spring6restmvcapi.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderShipmentUpdateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private static final Integer DEFAULT_PAGE = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<BeerOrderDTO> listBeerOrders(Integer pageNumber, Integer pageSize) {
        return beerOrderRepository
                .findAll(buildPageRequest(pageNumber, pageSize))
                .map(beerOrderMapper::beerOrderToBeerOrderDto);
    }

    @Override
    public Optional<BeerOrderDTO> getBeerOrderById(UUID beerOrderId) {
        return Optional.ofNullable(
                beerOrderMapper.beerOrderToBeerOrderDto(
                        beerOrderRepository.findById(beerOrderId).orElse(null)
                )
        );
    }

    @Override
    public BeerOrder createBeerOrder(BeerOrderCreateDTO beerOrderCreateDTO) {
        val customer = customerRepository
                .findById(beerOrderCreateDTO.getCustomerId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Customer not found with id %s", beerOrderCreateDTO.getCustomerId())));

        val beerOrderLines = beerOrderCreateDTO.getBeerOrderLines().stream()
                .map(orderLine -> {
                    val beer = beerRepository
                            .findById(orderLine.getBeerId())
                            .orElseThrow(() -> new NotFoundException(
                                    String.format("Beer not found with id %s", orderLine.getBeerId())));

                    return BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(orderLine.getOrderQuantity())
                            .build();
                })
                .collect(Collectors.toSet());

        val beerOrder = BeerOrder.builder()
                .customerRef(beerOrderCreateDTO.getCustomerRef())
                .customer(customer)
                .beerOrderLines(beerOrderLines)
                .build();

        return beerOrderRepository.save(beerOrder);
    }

    @Override
    public BeerOrderDTO updateBeerOrder(UUID beerOrderId, BeerOrderUpdateDTO beerOrderUpdateDTO) {
        val beerOrder = beerOrderRepository.findById(beerOrderId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Beer order not found with id %s", beerOrderId)));

        beerOrder.setCustomerRef(beerOrderUpdateDTO.getCustomerRef());

        if (beerOrder.getCustomer().getId() != beerOrderUpdateDTO.getCustomerId()) {
            val customer = customerRepository.findById(beerOrderUpdateDTO.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found with id " + beerOrderUpdateDTO.getCustomerId()));
            beerOrder.setCustomer(customer);
        }

        val beerOrderLineUpdateDTOS = beerOrderUpdateDTO.getBeerOrderLines();
        if (beerOrderLineUpdateDTOS != null) {
            val beerOrderLines = beerOrderLineUpdateDTOS.stream()
                    .map(beerOrderLineUpdateDTO -> {
                        val beerOrderLine = beerOrder.getBeerOrderLines()
                                .stream()
                                .filter(foundOrderLine -> foundOrderLine.getId().equals(beerOrderLineUpdateDTO.getId()))
                                .findAny()
                                .orElseGet(BeerOrderLine::new);

                        val beer = beerRepository.findById(beerOrderLineUpdateDTO.getBeerId())
                                .orElseThrow(() -> new NotFoundException(
                                        String.format("Beer not found with id %s", beerOrderLineUpdateDTO.getBeerId())));

                        beerOrderLine.setBeer(beer);
                        beerOrderLine.setOrderQuantity(beerOrderLineUpdateDTO.getOrderQuantity());
                        beerOrderLine.setQuantityAllocated(beerOrderLineUpdateDTO.getQuantityAllocated());
                        return beerOrderLine;
                    })
                    .collect(Collectors.toSet());

            beerOrder.setBeerOrderLines(beerOrderLines);
        }

        BeerOrderShipmentUpdateDTO shipmentDto = beerOrderUpdateDTO.getBeerOrderShipment();
        if (shipmentDto != null && shipmentDto.getTrackingNumber() != null) {
            if (beerOrder.getBeerOrderShipment() == null) {
                beerOrder.setBeerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber(shipmentDto.getTrackingNumber())
                        .build());
            } else {
                beerOrder.getBeerOrderShipment().setTrackingNumber(shipmentDto.getTrackingNumber());
            }
        }

        BeerOrderDTO dto = beerOrderMapper.beerOrderToBeerOrderDto(beerOrderRepository.save(beerOrder));

        if (beerOrderUpdateDTO.getPaymentAmount() != null) {
            applicationEventPublisher.publishEvent(OrderPlacedEvent.builder()
                    .beerOrderDTO(dto)
                    .build());
        }

        return dto;
    }

    @Override
    public void deleteBeerOrder(UUID beerOrderId) {
        if (beerOrderRepository.existsById(beerOrderId)) {
            beerOrderRepository.deleteById(beerOrderId);
        } else {
            throw new NotFoundException(String.format("Beer order not found with id %s", beerOrderId));
        }
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        queryPageNumber = pageNumber != null && pageNumber >= 0
                ? pageNumber
                : DEFAULT_PAGE;

        queryPageSize = pageSize != null && pageSize > 0
                ? Math.min(pageSize, 1000)
                : DEFAULT_PAGE_SIZE;

        Sort sort = Sort.by(Sort.Order.asc("customer.name"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }
}
