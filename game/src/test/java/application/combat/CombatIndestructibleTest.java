package application.combat;

import application.AbstractApplicationTest;
import application.InitTestServiceDecorator;
import application.testcategory.Regression;
import com.matag.cards.Cards;
import com.matag.game.MatagGameApplication;
import com.matag.game.init.test.InitTestService;
import com.matag.game.status.GameStatus;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static application.browser.BattlefieldHelper.*;
import static com.matag.game.turn.phases.BeginCombatPhase.BC;
import static com.matag.game.turn.phases.DeclareAttackersPhase.DA;
import static com.matag.game.turn.phases.Main2Phase.M2;
import static com.matag.player.PlayerType.OPPONENT;
import static com.matag.player.PlayerType.PLAYER;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatagGameApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({CombatIndestructibleTest.InitGameTestConfiguration.class})
@Category(Regression.class)
public class CombatIndestructibleTest extends AbstractApplicationTest {

  @Autowired
  private InitTestServiceDecorator initTestServiceDecorator;

  @Autowired
  private Cards cards;

  public void setupGame() {
    initTestServiceDecorator.setInitTestService(new CombatIndestructibleTest.InitTestServiceForTest());
  }

  @Test
  public void indestructible() {
    // When going to combat
    browser.player1().getActionHelper().clickContinue();
    browser.player2().getPhaseHelper().is(BC, PLAYER);
    browser.player2().getActionHelper().clickContinue();
    browser.player1().getPhaseHelper().is(DA, PLAYER);

    // When declare attacker
    browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(cards.get("Nyxborn Courser")).declareAsAttacker();
    browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(cards.get("Nyxborn Marauder")).declareAsAttacker();
    browser.player1().getActionHelper().clickContinue();

    // Declare blocker
    browser.player2().getPhaseHelper().is(DA, PLAYER);
    browser.player2().getActionHelper().clickContinue();
    browser.player2().getBattlefieldHelper(OPPONENT, COMBAT_LINE).getFirstCard(cards.get("Nyxborn Courser")).select();
    browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(cards.get("Nyxborn Brute")).declareAsBlocker();
    browser.player2().getBattlefieldHelper(OPPONENT, COMBAT_LINE).getFirstCard(cards.get("Nyxborn Marauder")).select();
    browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(cards.get("Nyxborn Colossus")).declareAsBlocker();
    browser.player2().getActionHelper().clickContinue();

    // Make a Stand
    browser.player1().getBattlefieldHelper(PLAYER, FIRST_LINE).getCard(cards.get("Plains"), 0).tap();
    browser.player1().getBattlefieldHelper(PLAYER, FIRST_LINE).getCard(cards.get("Plains"), 1).tap();
    browser.player1().getBattlefieldHelper(PLAYER, FIRST_LINE).getCard(cards.get("Plains"), 2).tap();
    browser.player1().getHandHelper(PLAYER).getFirstCard(cards.get("Make a Stand")).click();
    browser.player2().getActionHelper().clickContinue();
    browser.player1().getActionHelper().clickContinue();
    browser.player2().getActionHelper().clickContinue();

    // Then
    browser.player1().getPhaseHelper().is(M2, PLAYER);
    browser.player1().getGraveyardHelper(PLAYER).containsExactly(cards.get("Make a Stand"));
    browser.player1().getGraveyardHelper(OPPONENT).containsExactly(cards.get("Nyxborn Brute"));

    // Finally indestructible still gets destroyed if toughness reaches 0
    browser.player1().getActionHelper().clickContinue();
    browser.player2().getBattlefieldHelper(PLAYER, FIRST_LINE).getCard(cards.get("Swamp"), 0).tap();
    browser.player2().getHandHelper(PLAYER).getFirstCard(cards.get("Disfigure")).select();
    browser.player2().getBattlefieldHelper(OPPONENT, SECOND_LINE).getFirstCard(cards.get("Nyxborn Marauder")).target();
    browser.player1().getActionHelper().clickContinue();
    browser.player2().getBattlefieldHelper(PLAYER, FIRST_LINE).getCard(cards.get("Swamp"), 1).tap();
    browser.player2().getHandHelper(PLAYER).getFirstCard(cards.get("Disfigure")).select();
    browser.player2().getBattlefieldHelper(OPPONENT, SECOND_LINE).getFirstCard(cards.get("Nyxborn Marauder")).target();
    browser.player1().getActionHelper().clickContinue();
    browser.player1().getGraveyardHelper(PLAYER).contains(cards.get("Nyxborn Marauder"));
  }

  static class InitTestServiceForTest extends InitTestService {
    @Override
    public void initGameStatus(GameStatus gameStatus) {
      addCardToCurrentPlayerBattlefield(gameStatus, cards.get("Plains"));
      addCardToCurrentPlayerBattlefield(gameStatus, cards.get("Plains"));
      addCardToCurrentPlayerBattlefield(gameStatus, cards.get("Plains"));
      addCardToCurrentPlayerBattlefield(gameStatus, cards.get("Nyxborn Courser"));
      addCardToCurrentPlayerBattlefield(gameStatus, cards.get("Nyxborn Marauder"));
      addCardToCurrentPlayerHand(gameStatus, cards.get("Make a Stand"));

      addCardToNonCurrentPlayerBattlefield(gameStatus, cards.get("Swamp"));
      addCardToNonCurrentPlayerBattlefield(gameStatus, cards.get("Swamp"));
      addCardToNonCurrentPlayerHand(gameStatus, cards.get("Disfigure"));
      addCardToNonCurrentPlayerHand(gameStatus, cards.get("Disfigure"));
      addCardToNonCurrentPlayerBattlefield(gameStatus, cards.get("Nyxborn Colossus"));
      addCardToNonCurrentPlayerBattlefield(gameStatus, cards.get("Nyxborn Brute"));
    }
  }
}
