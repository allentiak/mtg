package integration.mtg.game.turn.action.draw;

import com.aa.mtg.cardinstance.CardInstance;
import com.aa.mtg.cardinstance.CardInstanceFactory;
import com.aa.mtg.cardinstance.ability.CardInstanceAbility;
import com.aa.mtg.cards.Cards;
import com.aa.mtg.cards.ability.Ability;
import com.aa.mtg.cards.ability.type.AbilityType;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.draw.DrawXCardsAction;
import integration.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DrawTestConfiguration.class)
public class DrawXCardsActionTest {

    @Autowired
    private DrawXCardsAction drawXCardsAction;
    
    @Autowired
    private CardInstanceFactory cardInstanceFactory;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private Cards cards;

    @Test
    public void controllerDraw3Cards() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstance cardInstance = cardInstanceFactory.create(gameStatus, 12, cards.get("Dark Remedy"), "opponent-name");
        CardInstanceAbility draw2Cards = new CardInstanceAbility(Ability.builder().abilityType(AbilityType.DRAW_X_CARDS).parameters(singletonList("2")).build());

        // When
        drawXCardsAction.perform(cardInstance, gameStatus, draw2Cards);

        // Then
        assertThat(gameStatus.getPlayerByName("opponent-name").getHand().size()).isEqualTo(9);
        assertThat(gameStatus.getPlayerByName("opponent-name").getLibrary().size()).isEqualTo(31);
    }

    @Test
    public void currentPlayerDraw1Card() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstanceAbility draw1Cards = new CardInstanceAbility(Ability.builder().abilityType(AbilityType.DRAW_X_CARDS).parameters(singletonList("1")).build());

        // When
        drawXCardsAction.perform(null, gameStatus, draw1Cards);

        // Then
        assertThat(gameStatus.getPlayerByName("player-name").getHand().size()).isEqualTo(8);
        assertThat(gameStatus.getPlayerByName("player-name").getLibrary().size()).isEqualTo(32);
    }
}