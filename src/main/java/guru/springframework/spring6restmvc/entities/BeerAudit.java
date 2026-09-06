package guru.springframework.spring6restmvc.entities;

import guru.springframework.spring6restmvcapi.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerAudit {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID auditId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    private Integer version;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @Size(max = 50)
    @Column(length = 50)
    private String beerName;

    private BeerStyle beerStyle;

    @Size(max = 255)
    private String upc;

    private Integer quantityOnHand;

    private BigDecimal price;

    @CreationTimestamp
    private LocalDateTime createdDateAudit;

    private String principalName;

    private String auditEventType;
}