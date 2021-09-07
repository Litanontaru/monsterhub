package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.Creature
import java.math.BigDecimal

object CreatureSpeedService {
  private val MODS = mapOf(
      "Водный" to CreatureSpeed("Под водой", BigDecimal("1"))
  )

  private val MODIFIERS = mapOf(
      "Водный" to CreatureSpeed("Под водой", BigDecimal("1"), listOf("Водный")),
      "Галоп" to CreatureSpeed("По земле", BigDecimal("1"), listOf("Галоп")),
      "Гусеничный" to CreatureSpeed("По земле", BigDecimal("0.5"), listOf("Не может прыгать", "Повышенная проходимость")),
      "Колёсный" to CreatureSpeed("По земле", BigDecimal("2"), listOf("Не может прыгать", "Пониженная проходимость")),
      "Обычное плавание" to CreatureSpeed("Под водой", BigDecimal("0.1")),
      "Околоводный" to CreatureSpeed("Под водой", BigDecimal("0.3"), listOf("Околоводный")),
      "Парящий" to CreatureSpeed("Во воздухе", BigDecimal("2")),
      "Перемещение под землёй" to CreatureSpeed("Под землёй", BigDecimal("0.2")),
      "Ползающий" to CreatureSpeed("По земле", BigDecimal("0.5"), listOf("Быстрое лазание", "Повышенная проходимость")),
      "Форсаж" to CreatureSpeed("Во воздухе", BigDecimal("1"), listOf("Форсаж")),
      "Хождение по стенам" to CreatureSpeed("По земле", BigDecimal("1"), listOf("Хождение по стенам")),
      "Шаг" to CreatureSpeed("По земле", BigDecimal("1")),
      "Эфирный" to CreatureSpeed("Эфирный", BigDecimal("0.25"))
  )

  fun speed(creature: Creature): List<CreatureSpeed> {
    val size = CreatureService.size(creature)
    val partsSize = CreatureService.partsSize(creature)

    return creature
        .getAllTraits("Движение")
        .filter { it.feature.name != "Иное движение" }
        .mapNotNull { MODIFIERS[it.feature.name] ?.plus(it.x.toInt()) }
        .groupBy { it.mode }
        .mapValues { it.value.reduce { a, b -> a * b } }
        .values
        .map { it * SizeProfileService.get(size + it.modifierSize, partsSize + it.modifierSize) }
  }

  data class CreatureSpeed(
      val mode: String,
      val step: BigDecimal,
      val features: List<String> = listOf(),
      val modifierSize: Int = 0
  ) {
    val dash = step * BigDecimal.valueOf(5)

    operator fun plus(right: Int) = CreatureSpeed(
        mode,
        step,
        features,
        modifierSize + right
    )

    operator fun times(right: CreatureSpeed) = CreatureSpeed(
        mode,
        step * right.step,
        features + right.features,
        modifierSize + right.modifierSize
    )

    operator fun times(size: SizeProfile) = CreatureSpeed(
        mode,
        step * size.speedModifier,
        features,
        modifierSize
    )

    fun display(): String = "$mode: ${step.stripTrailingZeros()} м, рывок ${dash.stripTrailingZeros()} м ${features.joinToString()}"
  }
}