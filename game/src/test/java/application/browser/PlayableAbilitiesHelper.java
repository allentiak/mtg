package application.browser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class PlayableAbilitiesHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlayableAbilitiesHelper.class);

  private MatagBrowser matagBrowser;

  PlayableAbilitiesHelper(MatagBrowser matagBrowser) {
    this.matagBrowser = matagBrowser;
  }

  public void toHaveAbilities(List<String> expectedAbilities) {
    matagBrowser.wait(driver -> {
      List<String> abilities = abilities().stream().map(ability -> ability.getAttribute("title")).collect(Collectors.toList());
      LOGGER.info("abilities={}   expectedAbilities={}", abilities, expectedAbilities);
      return expectedAbilities.equals(abilities);
    });
  }

  public void playAbility(int index) {
    abilities().get(index).click();
  }

  private List<WebElement> abilities() {
    return getElementContainer().findElements(By.tagName("li"));
  }

  private WebElement getElementContainer() {
    return matagBrowser.findElement(By.id("playable-abilities"));
  }
}
