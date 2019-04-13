export default class CardUtils {
  static normalizeCardName(cardName) {
    return cardName.toLowerCase()
      .replace(' ', '_')
      .replace(',', '_')
      .replace('\'', '')
  }

  static hasSummoningSickness(cardInstance) {
    return cardInstance.modifiers.summoningSickness
  }

  static hasAbility(cardInstance, ability) {
    return cardInstance.abilities.includes(ability)
  }

  static frontendTap(cardInstance) {
    cardInstance.modifiers.tapped = 'FRONTEND_TAPPED'
  }

  static untap(cardInstance) {
    cardInstance.modifiers.tapped = undefined
  }

  static isFrontendTapped(cardInstance) {
    return cardInstance.modifiers.tapped === 'FRONTEND_TAPPED'
  }

  static toggleFrontendTapped(cardInstance) {
    if (CardUtils.isUntapped(cardInstance)) {
      CardUtils.frontendTap(cardInstance)
    } else if (CardUtils.isFrontendTapped(cardInstance)) {
      CardUtils.untap(cardInstance)
    }
  }

  static isNotFrontendBlocking(cardInstance) {
    return !CardUtils.isFrontendBlocking(cardInstance)
  }

  static isFrontendBlocking(cardInstance) {
    return cardInstance.modifiers.blocking === 'FRONTEND'
  }

  static setFrontendBlocking(cardInstance, blockingCardId) {
    cardInstance.modifiers.blocking = 'FRONTEND'
    cardInstance.modifiers.blockingCardId = blockingCardId
  }

  static setFrontendUnblocking(cardInstance) {
    cardInstance.modifiers.blocking = undefined
    cardInstance.modifiers.blockingCardId = undefined
  }

  static isNotFrontendAttacking(cardInstance) {
    return !CardUtils.isFrontendAttacking(cardInstance)
  }

  static isFrontendAttacking(cardInstance) {
    return cardInstance.modifiers.attacking === 'FRONTEND'
  }

  static setFrontendAttacking(cardInstance) {
    cardInstance.modifiers.attacking = 'FRONTEND'
  }

  static setFrontendUnattacking(cardInstance) {
    cardInstance.modifiers.attacking = undefined
  }

  static isBlocking(cardInstance) {
    return cardInstance.modifiers.blocking === true
  }

  static isBlockingOrFrontendBlocking(cardInstance) {
    return CardUtils.isBlocking(cardInstance) || CardUtils.isFrontendBlocking(cardInstance)
  }

  static isNotBlockingOrFrontendBlocking(cardInstance) {
    return CardUtils.isNotBlocking(cardInstance) && CardUtils.isNotFrontendBlocking(cardInstance)
  }

  static isNotBlocking(cardInstance) {
    return !CardUtils.isBlocking(cardInstance)
  }

  static isAttacking(cardInstance) {
    return cardInstance.modifiers.attacking === true
  }

  static isAttackingOrFrontendAttacking(cardInstance) {
    return CardUtils.isAttacking(cardInstance) || CardUtils.isFrontendAttacking(cardInstance)
  }

  static isNotAttacking(cardInstance) {
    return !CardUtils.isAttacking(cardInstance)
  }

  static isNotAttackingOrFrontendAttacking(cardInstance) {
    return CardUtils.isNotAttacking(cardInstance) && CardUtils.isNotFrontendAttacking(cardInstance)
  }

  static toggleFrontendBlocking(cardInstance, blockingCardId) {
    if (CardUtils.isNotFrontendBlocking(cardInstance)) {
      CardUtils.setFrontendBlocking(cardInstance, blockingCardId)
    } else if (CardUtils.isFrontendBlocking(cardInstance)) {
      CardUtils.setFrontendUnblocking(cardInstance)
    }
  }

  static toggleFrontendAttacking(cardInstance) {
    if (CardUtils.isNotFrontendAttacking(cardInstance)) {
      CardUtils.setFrontendAttacking(cardInstance)
    } else if (CardUtils.isFrontendAttacking(cardInstance)) {
      CardUtils.setFrontendUnattacking(cardInstance)
    }
  }

  static isUntapped(cardInstance) {
    return !CardUtils.isTappedOrFrontendTapped(cardInstance)
  }

  static isTapped(cardInstance) {
    return cardInstance.modifiers.tapped === 'TAPPED'
  }

  static isTappedOrFrontendTapped(cardInstance) {
    return CardUtils.isTapped(cardInstance) || CardUtils.isFrontendTapped(cardInstance)
  }

  static isOfType(cardInstance, type) {
    return cardInstance.card.types.includes(type)
  }

  static blockingCreaturesToTargetIdsEvent(blockingCreatures) {
    const map = {}

    blockingCreatures.forEach(blockingCreature => {
      map[blockingCreature.id] = [blockingCreature.modifiers.blockingCardId]
    })

    return map
  }
}
