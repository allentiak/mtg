package com.matag.game.turn.action.leave;

import com.matag.game.cardinstance.CardInstance;
import com.matag.game.status.GameStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReturnPermanentToHandService {
  private final LeaveBattlefieldService leaveBattlefieldService;
  private final PutIntoHandService putIntoHandService;

  public void markAsToBeReturnedToHand(GameStatus gameStatus, int targetId) {
    CardInstance cardInstance = gameStatus.findCardByIdFromAnyBattlefield(targetId);

    if (cardInstance != null) {
      cardInstance.getModifiers().setToBeReturnedToHand(true);
    }
  }

  public boolean returnPermanentToHand(GameStatus gameStatus, int targetId) {
    CardInstance cardInstance = gameStatus.findCardByIdFromAnyBattlefield(targetId);

    if (cardInstance != null) {
      leaveBattlefieldService.leaveTheBattlefield(gameStatus, targetId);
      putIntoHandService.returnToHand(gameStatus, cardInstance);
      return true;
    }

    return false;
  }
}
