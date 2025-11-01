package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerCsvServiceImplTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    void convertCSV() throws IOException {
        ClassPathResource resource = new ClassPathResource("csvdata/beers.csv");

        try (InputStream inputStream = resource.getInputStream()) {
            List<BeerCSVRecord> recs = beerCsvService.convertCsv(inputStream);

            System.out.println(recs.size());

            assertThat(recs.size()).isGreaterThan(0);
        }
    }
}