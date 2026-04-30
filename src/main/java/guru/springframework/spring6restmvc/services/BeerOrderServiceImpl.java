package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private static final Integer DEFAULT_PAGE = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

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
