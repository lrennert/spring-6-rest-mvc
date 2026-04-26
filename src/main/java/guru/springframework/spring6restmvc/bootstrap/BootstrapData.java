package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerOrderRepository;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws IOException {
        loadBeerData();
        loadCsvData();
        loadCustomerData();
        loadOrderData();
    }

    private void loadCsvData() throws IOException {
        if (beerRepository.count() >= 10) {
            return;
        }

        Resource resource = new ClassPathResource("csvdata/beers.csv");

        try (InputStream inputStream = resource.getInputStream()) {
            List<BeerCSVRecord> recs = beerCsvService.convertCsv(inputStream);

            recs.forEach(beerCSVRecord -> {
                BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                    case "American Pale Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale",
                         "American Blonde Ale" -> BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };

                beerRepository.save(Beer.builder()
                        .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                        .beerStyle(beerStyle)
                        .price(BigDecimal.TEN)
                        .upc(beerCSVRecord.getRow().toString())
                        .quantityOnHand(beerCSVRecord.getCount())
                        .build());
            });
        }
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer1 = Beer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356")
                    .quantityOnHand(122)
                    .price(new BigDecimal("12.99"))
                    .build();

            Beer beer2 = Beer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .beerName("Crank")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356222")
                    .quantityOnHand(392)
                    .price(new BigDecimal("11.99"))
                    .build();

            Beer beer3 = Beer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .beerName("Sunshine City")
                    .beerStyle(BeerStyle.IPA)
                    .upc("12356")
                    .quantityOnHand(144)
                    .price(new BigDecimal("13.99"))
                    .build();

            beerRepository.save(beer1);
            beerRepository.save(beer2);
            beerRepository.save(beer3);
        }
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer1 = Customer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .name("Customer 1")
                    .build();

            Customer customer2 = Customer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .name("Customer 2")
                    .build();

            Customer customer3 = Customer.builder()
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .name("Customer 3")
                    .build();

            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        }
    }

    private void loadOrderData() {
        if (beerOrderRepository.count() == 0) {
            val beers = beerRepository.findAll();
            val beerIterator = beers.iterator();

            customerRepository.findAll().forEach(customer -> {
                BeerOrder beerOrder1 = createBeerOrder(customer, 1, List.of(beerIterator.next(), beerIterator.next()));
                BeerOrder beerOrder2 = createBeerOrder(customer, 2, List.of(beerIterator.next(), beerIterator.next()));
                beerOrderRepository.saveAll(Arrays.asList(beerOrder1, beerOrder2));
            });

            // for debugging
            // val orders = beerOrderRepository.findAll();
        }
    }

    private BeerOrder createBeerOrder(Customer customer, int orderNumber, List<Beer> beers) {
        return BeerOrder.builder()
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .customerRef("Ref_" + customer.getId() + "_" + orderNumber)
                .customer(customer)
                .beerOrderLines(Set.of(
                        BeerOrderLine.builder()
                                .createdDate(LocalDateTime.now())
                                .lastModifiedDate(LocalDateTime.now())
                                .beer(beers.getFirst())
                                .orderQuantity(10)
                                .build(),
                        BeerOrderLine.builder()
                                .createdDate(LocalDateTime.now())
                                .lastModifiedDate(LocalDateTime.now())
                                .beer(beers.get(1))
                                .orderQuantity(20)
                                .build())
                )
                .build();
    }
}
