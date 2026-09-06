package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.services.BeerOrderService;
import guru.springframework.spring6restmvcapi.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerOrderController {

    public static final String BEER_ORDERS_PATH = "/api/v1/beer-orders";
    public static final String BEER_ORDERS_PATH_ID = BEER_ORDERS_PATH + "/{beerOrderId}";

    private final BeerOrderService beerOrderService;

    @GetMapping(value = BEER_ORDERS_PATH)
    public Page<BeerOrderDTO> listBeerOrders(@RequestParam(required = false) Integer pageNumber,
                                             @RequestParam(required = false) Integer pageSize) {
        return beerOrderService.listBeerOrders(pageNumber, pageSize);
    }

    @GetMapping(value = BEER_ORDERS_PATH_ID)
    public BeerOrderDTO getBeerOrderById(@PathVariable("beerOrderId") UUID beerOrderId) {
        return beerOrderService.getBeerOrderById(beerOrderId)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping(value = BEER_ORDERS_PATH)
    public ResponseEntity<Void> createBeerOrder(@Validated @RequestBody BeerOrderCreateDTO beerOrderCreateDTO) {
        BeerOrder beerOrder = beerOrderService.createBeerOrder(beerOrderCreateDTO);
        URI location = URI.create(BEER_ORDERS_PATH + "/" + beerOrder.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = BEER_ORDERS_PATH_ID)
    public ResponseEntity<BeerOrderDTO> updateBeerOrder(@PathVariable("beerOrderId") UUID beerOrderId,
                                                        @Validated @RequestBody BeerOrderUpdateDTO beerOrderUpdateDTO) {
        BeerOrderDTO beerOrderDTO = beerOrderService.updateBeerOrder(beerOrderId, beerOrderUpdateDTO);
        return ResponseEntity.ok(beerOrderDTO);
    }

    @DeleteMapping(value = BEER_ORDERS_PATH_ID)
    public ResponseEntity<Void> deleteBeerOrder(@PathVariable("beerOrderId") UUID beerOrderId) {
        beerOrderService.deleteBeerOrder(beerOrderId);
        return ResponseEntity.noContent().build();
    }
}
