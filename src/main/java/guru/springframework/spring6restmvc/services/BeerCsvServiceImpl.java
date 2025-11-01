package guru.springframework.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

	@Override
	public List<BeerCSVRecord> convertCsv(InputStream csvStream) {
		try (Reader reader = new InputStreamReader(csvStream)){
            return new CsvToBeanBuilder<BeerCSVRecord>(reader)
                    .withType(BeerCSVRecord.class)
                    .build().parse();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read CSV data", e);
		}
	}
}
