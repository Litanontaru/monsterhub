package org.dmg.monsterhub.data.meta

import org.dmg.isZero
import java.math.BigDecimal

enum class NumberOption(

    val displayName: String,

    val format: (BigDecimal, BigDecimal, BigDecimal) -> Sequence<String>,

    val fieldsCount: Int

) {
  NONE("Нет", { _, _, _ -> none() }, 0),
  POSITIVE("Позитивные", { x, _, _ -> positive(x) }, 1),
  POSITIVE_AND_INFINITE("Позитивные и бесконечность", { x, _, _ -> positiveAndInfinite(x) }, 1),
  FREE("Любые целые", { x, _, _ -> free(x) }, 1),
  DAMAGE("Урон", { x, xa, _ -> damage(x, xa) }, 2),
  ARMOR("Очки брони", { x, xa, xb -> armor(x, xa, xb) }, 3),
  IMPORTANCE("Важность", { x, _, _ -> importance(x) }, 1);

  fun context(x: BigDecimal, xa: BigDecimal, xb: BigDecimal) = when (fieldsCount) {
    0 -> listOf()
    1 -> listOf(x)
    2 -> listOf(x, xa)
    3 -> listOf(x, xa, xb)
    else -> throw IllegalArgumentException()
  }

  companion object {
    val IMPORTANCE_OPTIONS = listOf(
        "Никогда или Никакую роль",
        "Малую Редко",
        "Важную Редко",
        "Малую Вероятно",
        "Эпическую Редко",
        "Малую Часто",
        "Важную Вероятно",
        "Важную Часто",
        "Эпическую Вероятно",
        "Эпическую Часто"
    )

    val display = values().asSequence().map { it.displayName }.toList()

    operator fun invoke(displayName: String): NumberOption = values().find { it.displayName == displayName } ?: NONE

    private fun seq(x: BigDecimal): Sequence<String> = sequenceOf(x.stripTrailingZeros().toPlainString())

    private fun none() = sequenceOf<String>()

    private fun positive(x: BigDecimal) = if (x.isZero()) sequenceOf() else seq(x)

    private fun positiveAndInfinite(x: BigDecimal) = when {
      x.isZero() -> sequenceOf()
      x.toInt() == Int.MAX_VALUE -> sequenceOf("Бесконечность")
      else -> seq(x)
    }

    private fun free(x: BigDecimal) = seq(x)

    private fun damage(x: BigDecimal, xa: BigDecimal) = when {
      x.isZero() && xa.isZero() -> sequenceOf()
      else -> sequenceOf("${x.stripTrailingZeros().toPlainString()}/${xa.stripTrailingZeros().toPlainString()}")
    }

    private fun armor(x: BigDecimal, xa: BigDecimal, xb: BigDecimal) = when {
      x.isZero() && xa.isZero() && xb.isZero() -> sequenceOf()
      else -> sequenceOf("${x.stripTrailingZeros().toPlainString()}/${xa.stripTrailingZeros().toPlainString()}/${xb.stripTrailingZeros().toPlainString()}")
    }

    private fun importance(x: BigDecimal) = sequenceOf(IMPORTANCE_OPTIONS[x.toInt()])
  }
}