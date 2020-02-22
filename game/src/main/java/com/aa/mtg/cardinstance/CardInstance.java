package com.aa.mtg.cardinstance;

import com.aa.mtg.cardinstance.ability.CardInstanceAbility;
import com.aa.mtg.cardinstance.modifiers.CardModifiers;
import com.aa.mtg.cards.Card;
import com.aa.mtg.cards.CardUtils;
import com.aa.mtg.cards.ability.trigger.TriggerSubtype;
import com.aa.mtg.cards.ability.trigger.TriggerType;
import com.aa.mtg.cards.ability.type.AbilityType;
import com.aa.mtg.cards.properties.Color;
import com.aa.mtg.cards.properties.Subtype;
import com.aa.mtg.cards.properties.Type;
import com.aa.mtg.game.message.MessageException;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.attach.AttachmentsService;
import com.aa.mtg.game.turn.action.selection.AbilitiesFromOtherPermanentsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.aa.mtg.cardinstance.ability.CardInstanceAbility.getCardInstanceAbilities;
import static com.aa.mtg.cards.ability.trigger.TriggerType.MANA_ABILITY;
import static com.aa.mtg.cards.ability.type.AbilityType.*;
import static com.aa.mtg.cards.properties.Type.INSTANT;
import static com.aa.mtg.cards.properties.Type.SORCERY;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@Component
@Scope("prototype")
public class CardInstance {

    private int id;
    @JsonProperty private Card card;
    @JsonProperty private String owner;
    private String controller;
    @JsonProperty private CardModifiers modifiers = new CardModifiers();
    @JsonProperty private List<CardInstanceAbility> triggeredAbilities = new ArrayList<>();
    private Set<String> acknowledgeBy = new HashSet<>();

    private GameStatus gameStatus;
    private final AttachmentsService attachmentsService;
    private final AbilitiesFromOtherPermanentsService abilitiesFromOtherPermanentsService;
    @Autowired
    public CardInstance(
            @Autowired(required = false) AttachmentsService attachmentsService,
            @Autowired(required = false) AbilitiesFromOtherPermanentsService abilitiesFromOtherPermanentsService
    ) {
        this.attachmentsService = attachmentsService;
        this.abilitiesFromOtherPermanentsService = abilitiesFromOtherPermanentsService;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @JsonProperty
    public int getId() {
        return modifiers.getPermanentId() > 0 ? modifiers.getPermanentId() : id;
    }

    public String getIdAndName() {
        return "\"" + getId() + " - " + card.getName() + "\"";
    }

    public Card getCard() {
        return card;
    }

    public String getOwner() {
        return owner;
    }

    @JsonProperty
    public String getController() {
        if (modifiers.getControllerUntilEndOfTurn() != null) {
            return modifiers.getControllerUntilEndOfTurn();
        }

        if (modifiers.getController() != null) {
            return modifiers.getController();
        }

        return controller;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public CardModifiers getModifiers() {
        return modifiers;
    }

    public List<CardInstanceAbility> getTriggeredAbilities() {
        return triggeredAbilities;
    }

    public boolean isOfType(Type type) {
        return CardUtils.isOfType(card, type);
    }

    public boolean ofAnyOfTheTypes(List<Type> types) {
        for (Type type : types) {
            if (isOfType(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean ofAnyOfTheSubtypes(List<Subtype> subtypes) {
        for (Subtype subtype : subtypes) {
            if (isOfSubtype(subtype)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOfColor(Color color) {
        return CardUtils.isOfColor(card, color);
    }

    public boolean ofAnyOfTheColors(List<Color> colors) {
        for (Color color : colors) {
            if (isOfColor(color)) {
                return true;
            }
        }
        return false;
    }

    public void checkIfCanAttack() {
        if (!isOfType(Type.CREATURE)) {
            throw new MessageException(getIdAndName() + " is not of type Creature.");
        }

        if (modifiers.isTapped()) {
            throw new MessageException(getIdAndName() + " is already tapped and cannot attack.");
        }

        if (isSummoningSickness()) {
            throw new MessageException(getIdAndName() + " has summoning sickness and cannot attack.");
        }
    }

    @JsonProperty
    public boolean isSummoningSickness() {
        return modifiers.isSummoningSickness() && !hasAbilityType(HASTE);
    }

    public void declareAsAttacker() {
        if (!hasAbilityType(VIGILANCE)) {
            modifiers.tap();
        }
        modifiers.setAttacking(true);
    }

    public void checkIfCanBlock(CardInstance blockedCreature) {
        if (!isOfType(Type.CREATURE)) {
            throw new MessageException(getIdAndName() + " is not of type Creature.");
        }

        if (modifiers.isTapped()) {
            throw new MessageException(getIdAndName() + " is tapped and cannot block.");
        }

        if (blockedCreature.hasAbilityType(FLYING)) {
            if (!(hasAbilityType(FLYING) || hasAbilityType(REACH))) {
                throw new MessageException(getIdAndName() + " cannot block " + blockedCreature.getIdAndName() + " as it has flying.");
            }
        }
    }

    public void declareAsBlocker(int attackingCreatureId) {
        modifiers.setBlockingCardId(attackingCreatureId);
    }

    @JsonProperty
    public int getPower() {
        return card.getPower() +
                modifiers.getExtraPowerToughnessUntilEndOfTurn().getPower() +
                modifiers.getExtraPowerToughnessFromCounters().getPower() +
                getAttachmentsPower() +
                getPowerFromOtherPermanents();
    }

    @JsonProperty
    public int getToughness() {
        return card.getToughness() +
                modifiers.getExtraPowerToughnessUntilEndOfTurn().getToughness() +
                modifiers.getExtraPowerToughnessFromCounters().getToughness() +
                getAttachmentsToughness() +
                getToughnessFromOtherPermanents();
    }

    @JsonProperty
    public List<CardInstanceAbility> getAbilities() {
        List<CardInstanceAbility> abilities = getFixedAbilities();
        abilities.addAll(getAbilitiesFormOtherPermanents());
        return abilities;
    }

    public List<CardInstanceAbility> getFixedAbilities() {
        List<CardInstanceAbility> abilities = new ArrayList<>();
        abilities.addAll(getCardInstanceAbilities(card));
        abilities.addAll(modifiers.getAbilities());
        abilities.addAll(modifiers.getAbilitiesUntilEndOfTurn());
        abilities.addAll(getAttachmentsAbilities());
        return abilities;
    }

    public boolean canProduceMana(Color color) {
        return getAbilitiesByTriggerType(MANA_ABILITY).stream()
                .flatMap(ability -> ability.getParameters().stream())
                .anyMatch(parameter -> parameter.equals(color.toString()));
    }

    public List<CardInstanceAbility> getAbilitiesByTriggerType(TriggerType triggerType) {
        return getAbilities().stream()
                .filter(ability -> ability.getTrigger() != null)
                .filter(ability -> ability.getTrigger().getType().equals(triggerType))
                .collect(toList());
    }

    public List<CardInstanceAbility> getAbilitiesByTriggerSubType(TriggerSubtype triggerSubType) {
        return getAbilities().stream()
                .filter(ability -> ability.getTrigger() != null)
                .filter(ability -> triggerSubType.equals(ability.getTrigger().getSubtype()))
                .collect(toList());
    }

    public List<CardInstanceAbility> getAbilitiesByType(AbilityType abilityType) {
        return getAbilities().stream()
            .filter(currentAbility -> currentAbility.getAbilityType().equals(abilityType))
            .collect(toList());
    }

    public boolean hasAbilityType(AbilityType abilityType) {
        return getAbilitiesByType(abilityType).size() > 0;
    }

    public List<CardInstanceAbility> getFixedAbilitiesByType(AbilityType abilityType) {
        return getFixedAbilities().stream()
                .filter(currentAbility -> currentAbility.getAbilityType().equals(abilityType))
                .collect(toList());
    }

    public boolean hasFixedAbility(AbilityType abilityType) {
        return getFixedAbilitiesByType(abilityType).size() > 0;
    }

    public boolean hasFixedAbilityWithTriggerSubType(TriggerSubtype triggerSubtype) {
        return getAbilitiesByTriggerSubType(triggerSubtype).size() > 0;
    }

    public boolean isPermanent() {
        return !(isOfType(INSTANT) || isOfType(SORCERY));
    }

    public boolean isOfSubtype(Subtype subtype) {
        return this.card.getSubtypes().contains(subtype);
    }

    private int getAttachmentsPower() {
        return attachmentsService != null ? attachmentsService.getAttachmentsPower(gameStatus, this) : 0;
    }

    private int getAttachmentsToughness() {
        return attachmentsService != null ? attachmentsService.getAttachmentsToughness(gameStatus, this) : 0;
    }

    private List<CardInstanceAbility> getAttachmentsAbilities() {
        return attachmentsService != null ? attachmentsService.getAttachmentsAbilities(gameStatus, this) : emptyList();
    }

    private int getPowerFromOtherPermanents() {
        return abilitiesFromOtherPermanentsService != null ? abilitiesFromOtherPermanentsService.getPowerFromOtherPermanents(gameStatus, this) : 0;
    }

    private int getToughnessFromOtherPermanents() {
        return abilitiesFromOtherPermanentsService != null ? abilitiesFromOtherPermanentsService.getToughnessFromOtherPermanents(gameStatus, this) : 0;
    }

    private List<CardInstanceAbility> getAbilitiesFormOtherPermanents() {
        return abilitiesFromOtherPermanentsService != null ? abilitiesFromOtherPermanentsService.getAbilitiesFormOtherPermanents(gameStatus, this) : emptyList();
    }

    public void cleanup() {
        modifiers.cleanupUntilEndOfTurnModifiers();
        acknowledgeBy.clear();
    }

    public void resetAllModifiers() {
        modifiers = new CardModifiers();
    }

    public Set<String> getAcknowledgedBy() {
        return acknowledgeBy;
    }

    public void acknowledgeBy(String playerName) {
        acknowledgeBy.add(playerName);
    }
}
