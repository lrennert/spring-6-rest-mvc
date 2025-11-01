package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;

import java.io.InputStream;
import java.util.List;

public interface BeerCsvService {
	List<BeerCSVRecord> convertCsv(InputStream csvStream);
}
