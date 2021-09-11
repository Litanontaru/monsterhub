package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Perk
import org.dmg.monsterhub.data.Perk.Companion.PERK
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.Weapon.Companion.WEAPON
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.WeaponRepository
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

object AttackService {
  fun actions(creature: Creature, weaponRepository: WeaponRepository, settings: List<Setting>): List<Action> {
    val perks = creature
        .getAll(PERK)
        .map { it.feature as Perk }
        .toList()

    val groupSize = creature.getAllTraits("Группа").take(1).singleOrNull()?.x?.toInt() ?: 0

    val hasPowerAction = if (perks.any { it.name == "Мощные приёмы" }) 1 else 0
    val hasFinesseAction = if (perks.any { it.name == "Точные приёмы" }) 1 else 0

    val hasSpeedAction = if (perks.any { it.name == "Стремительные приёмы" }) 1 else 0
    val hasComboAction = if (perks.any { it.name == "Комбоатака" }) 1 else 0

    val manipulatorCount = creature.getAllTraits("Манипуляторы").map { it.x.toInt() }.sum()
    val baseAttackCount: Int = if (manipulatorCount <= 6) 1 else (manipulatorCount - 3) / 2

    val sizeProfile = CreatureService.sizeProfile(creature)
    val attackFeatures = creature.getAllTraits("Свойство атаки")
        .groupBy { it.designations.find { it.designationKey == "Естественное оружие" }!!.value }

    val naturalWeapons = CreatureService
        .naturalWeapons(creature)
        .let {
          val count = it.associateBy({ it.first }, { it.second })

          weaponRepository
              .findAllByNameInAndSettingIn(it.map { it.first }, settings)
              .map { it.adjust(sizeProfile, true, attackFeatures.getOrDefault(it.name, emptyList())) }
              .flatMap { weapon -> (1..count[weapon.name]!!).map { weapon } }
        }

    val weapons = naturalWeapons +
        creature
            .getAll(WEAPON)
            .map { it.feature as Weapon }
            .toList()

    val baseAttacks = weapons
        .asSequence()
        .flatMap { weapon ->
          val weaponId = UUID.randomUUID().toString()
          weapon.attacks.asSequence().map { weaponId to it }
        }
        .flatMap { (weaponId, weaponAttack) -> splitAttack(weaponId, weaponAttack, hasPowerAction, hasFinesseAction, groupSize) }
        .toList()

    return generateSequence(listOf(Action(listOf()))) { generateNext(it, baseAttacks, baseAttackCount, hasSpeedAction, hasComboAction) }
        .map { it.filter { it.speed >= -5 } }
        .takeWhile { it.isNotEmpty() }
        .flatMap { it.asSequence() }
        .filter { it.attacks.isNotEmpty() }
        .fold(mutableListOf()) { acc, action ->
          action.addTo(acc)
          acc
        }

  }

  private fun splitAttack(weaponId: String, weaponAttack: WeaponAttack, hasPowerAction: Int, hasFinesseAction: Int, groupSize: Int): Sequence<BaseAttack> {
    val result = mutableListOf<BaseAttack>()

    val power = weaponAttack.features.find { it.feature.name == "Мощное" }
    val powerRate = (power?.x?.toInt() ?: 0) + hasPowerAction
    if (powerRate > 0) {
      val powerType = (power
          ?.features
          ?.find { it.feature.featureType == "POWER_UP_TYPE" }
          ?.let { PowerUpType(it.feature.name) }
          ?: PowerUpType.MIX)

      val (bonusDamage, bonusDestruction) = powerType.calculator(powerRate)

      result += BaseAttack(weaponAttack.damage + bonusDamage, weaponAttack.desturction + groupSize + bonusDestruction, weaponAttack.distance, weaponAttack.speed, 0, weaponId, false)
    }

    val finesse = ((weaponAttack.features.find { it.feature.name == "Точное" }?.let { 1 } ?: 0) +
        (weaponAttack.features.find { it.feature.name == "Неточное" }?.let { -1 } ?: 0) + hasFinesseAction)
        .let { if (it < 0) 0 else it }

    if (finesse > 0) {
      result += BaseAttack(weaponAttack.damage, weaponAttack.desturction + groupSize, weaponAttack.distance, weaponAttack.speed, finesse, weaponId, false)
    }
    result += BaseAttack(weaponAttack.damage, weaponAttack.desturction + groupSize, weaponAttack.distance, weaponAttack.speed, 0, weaponId, true)

    return result.asSequence()
  }

  private fun generateNext(bases: List<Action>, baseAttacks: List<BaseAttack>, baseAttackCount: Int, hasSpeedAction: Int, hasComboAction: Int) = bases
      .flatMap { base ->
        baseAttacks.mapNotNull { baseAttack ->
          (base + baseAttack)
              .takeIf { it.attacks.size == 1 || it.simple }
              ?.also { it.speed(baseAttackCount, hasComboAction, hasSpeedAction) }
        }
      }
}

enum class PowerUpType(val key: String, val calculator: (Int) -> Pair<Int, Int>) {
  MIX("", { (it / 2 + it % 2) to (it / 2) }),
  DAMAGE("Урон", { it to 0 }),
  DESTRUCTION("Разрушение", { 0 to it });

  companion object {
    private val MAPPING = values().associateBy { it.key }

    operator fun invoke(key: String) = MAPPING[key] ?: error("Cannot find $key")
  }
}

data class Action(
    val attacks: List<BaseAttack>
) {
  val damageSum = attacks.sumBy { it.damage + it.destruction }
  val finesseSum = attacks.sumBy { it.finesse }
  val distance = attacks.map { it.distance }.min() ?: BigDecimal.ZERO
  val simple = attacks.all { it.simple }

  var speed = 0;
  var speedCategory = ""

  operator fun plus(baseAttack: BaseAttack): Action = Action((attacks + baseAttack).sortedBy { it.weaponId })

  fun speed(baseAttackCount: Int, hasComboAction: Int, hasSpeedAction: Int) = attacks
      .map { it.weaponId }
      .distinct()
      .size
      .let { combo ->
        val minSpeed = attacks.minBy { it.speed }!!.speed
        val repetition = (-5 + hasComboAction) * (attacks.size - combo)
        val difCombo = when {
          combo <= baseAttackCount -> 0
          combo == baseAttackCount + 1 -> -4 + hasComboAction
          else -> -100
        }

        minSpeed + repetition + difCombo + hasSpeedAction
      }.let {
        when {
          attacks.size == 1 && attacks[0].simple -> it + 1
          else -> it
        }
      }.also {
        speed = it
        speedCategory = speedCategory()
      }

  fun display() = speedCategory

  private fun speedCategory(): String {
    return when (speed) {
      -5, -4 -> "Очень медленный"
      -3, -2 -> "Медленный"
      0, -1, 1 -> "Обычный"
      2, 3 -> "Быстрый"
      4, 5, 6, 7, 8, 9 -> "Молниеносный"
      else -> "!!!"
    }
  }

  fun addTo(actions: MutableList<Action>) {
    actions.removeIf { this.compare(it) > 1 }
    if (actions.all { this.compare(it) >= 1 }) {
      actions.add(this)
    }
  }

  fun compare(other: Action): Int {
    var isBetter = false
    var isWorse = false
    if (speedCategory != other.speedCategory()) {
      when {
        speed > other.speed -> isBetter = true
        else -> isWorse = true
      }
    }
    when {
      damageSum > other.damageSum -> isBetter = true
      damageSum < other.damageSum -> isWorse = true
    }
    when {
      finesseSum > other.finesseSum -> isBetter = true
      finesseSum < other.finesseSum -> isWorse = true
    }
    when {
      distance > other.distance -> isBetter = true
      distance < other.distance -> isWorse = true
    }
    return when {
      isBetter -> if (isWorse) 1 else 2
      else -> if (isWorse) -1 else 0
    }
  }
}

data class BaseAttack(
    val damage: Int,
    val destruction: Int,
    val distance: BigDecimal,
    val speed: Int,
    val finesse: Int,

    val weaponId: String,
    val simple: Boolean
) {
  fun display(): String = "$damage/$destruction +${finesse}К, ${distance.round(MathContext(2)).stripTrailingZeros().toPlainString()} м"
}