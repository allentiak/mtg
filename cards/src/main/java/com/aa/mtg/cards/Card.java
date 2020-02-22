package com.aa.mtg.cards;

import com.aa.mtg.cards.ability.Ability;
import com.aa.mtg.cards.properties.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Card {
  private final String name;
  private final TreeSet<Color> colors;
  private final List<Cost> cost;
  private final TreeSet<Type> types;
  private final TreeSet<Subtype> subtypes;
  private final Rarity rarity;
  private final String ruleText;
  private final int power;
  private final int toughness;
  private final List<Ability> abilities;

  @JsonCreator
  public Card(@JsonProperty("name") String name,
      @JsonProperty("colors") TreeSet<Color> colors, @JsonProperty("cost") List<Cost> cost,
      @JsonProperty("types") TreeSet<Type> types, @JsonProperty("subtypes") TreeSet<Subtype> subtypes,
      @JsonProperty("rarity") Rarity rarity, @JsonProperty("ruleText") String ruleText,
      @JsonProperty("power") int power, @JsonProperty("toughness") int toughness,
      @JsonProperty("abilities") List<Ability> abilities) {
    this.name = name;
    this.colors = colors != null ? colors : new TreeSet<>();
    this.cost = cost != null ? cost : new ArrayList<>();
    this.types = types != null ? types : new TreeSet<>();
    this.subtypes = subtypes != null ? subtypes : new TreeSet<>();
    this.rarity = rarity;
    this.ruleText = ruleText != null ? ruleText : "";
    this.power = power;
    this.toughness = toughness;
    this.abilities = abilities != null ? abilities : new ArrayList<>();
  }

  public boolean isInstantSpeed() {
    return types.contains(Type.INSTANT);
  }
}
