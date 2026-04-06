package guru.springframework.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BeerOrderLineDTO {
    private UUID id;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private BeerDTO beer;
    private Integer orderQuantity;
    private Integer quantityAllocated;
}
