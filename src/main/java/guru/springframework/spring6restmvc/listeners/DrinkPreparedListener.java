package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvc.repositories.BeerOrderLineRepository;
import guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DrinkPreparedListener {

    @Autowired
    private BeerOrderLineRepository beerOrderLineRepository;

    @Transactional
    @KafkaListener(groupId = "DrinkPreparedListener", topics = KafkaConfig.DRINK_PREPARED_TOPIC)
    public void listenDrinkPrepared(@Payload DrinkPreparedEvent drinkPreparedEvent) {

        beerOrderLineRepository.findById(drinkPreparedEvent.getBeerOrderLineDTO().getId())
                .ifPresentOrElse(beerOrderLine -> {
                            beerOrderLine.setOrderLineStatus(BeerOrderLineStatus.COMPLETE);
                            beerOrderLineRepository.saveAndFlush(beerOrderLine);
                        }, () -> log.error("Beer Order Line not found."));
    }
}
