package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.events.BeerDeletedEvent;
import guru.springframework.spring6restmvc.events.BeerEvent;
import guru.springframework.spring6restmvc.events.BeerPatchedEvent;
import guru.springframework.spring6restmvc.events.BeerUpdatedEvent;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repositories.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerCreatedListener {

    private final BeerMapper beerMapper;
    private final BeerAuditRepository beerAuditRepository;

    @Async
    @EventListener
    public void listen(BeerEvent event) {

        val beerAudit = beerMapper.beerToBeerAudit(event.getBeer());

        String eventType;

        switch (event) {
            case BeerCreatedEvent _ -> eventType = "BEER_CREATED";
            case BeerUpdatedEvent _ -> eventType = "BEER_UPDATED";
            case BeerPatchedEvent _ -> eventType = "BEER_PATCHED";
            case BeerDeletedEvent _ -> eventType = "BEER_DELETED";
            default -> eventType = "UNKNOWN";
        }
        beerAudit.setAuditEventType(eventType);

        if (event.getBeer().getId() != null && event.getAuthentication().getName() != null) {
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        }

        val savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.debug("Beer Audit Saved: {}", savedBeerAudit.getId());
    }
}
