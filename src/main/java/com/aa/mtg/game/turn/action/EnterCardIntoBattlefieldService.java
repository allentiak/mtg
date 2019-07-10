package com.aa.mtg.game.turn.action;

import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.ability.Ability;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.status.GameStatusUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aa.mtg.cards.ability.trigger.TriggerSubtype.WHEN_IT_ENTERS_THE_BATTLEFIELD;

@Service
public class EnterCardIntoBattlefieldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnterCardIntoBattlefieldService.class);

    private final GameStatusUpdaterService gameStatusUpdaterService;

    @Autowired
    public EnterCardIntoBattlefieldService(GameStatusUpdaterService gameStatusUpdaterService) {
        this.gameStatusUpdaterService = gameStatusUpdaterService;
    }

    public void enter(GameStatus gameStatus, CardInstance cardInstance) {
        String controller = cardInstance.getController();
        gameStatus.getPlayerByName(controller).getBattlefield().addCard(cardInstance);

        for (Ability ability : cardInstance.getAbilities()) {
            if (ability.hasTriggerOfSubtype(WHEN_IT_ENTERS_THE_BATTLEFIELD)) {
                cardInstance.getTriggeredAbilities().add(ability);
                LOGGER.info("Event {} triggered with ability {} for {}.", WHEN_IT_ENTERS_THE_BATTLEFIELD, ability.getAbilityTypes(), cardInstance.getModifiers());
                gameStatus.getStack().add(cardInstance);
                gameStatusUpdaterService.sendUpdateStack(gameStatus);
                return;
            }
        }
    }

}
