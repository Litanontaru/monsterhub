package org.dmg.monsterhub.data.meta

enum class NumberOption(val displayName: String) {
  NONE("Нет"),
  POSITIVE("Позитивные"),
  POSITIVE_AND_INFINITE("Позитивные и бесконечность"),
  FREE("Любые целые"),
  DAMAGE("Урон"),
  ARMOR("Очки брони"),
  IMPORTANCE("Важность");

  companion object {
    val display = values().asSequence().map { it.displayName }.toList()

    operator fun invoke(displayName: String): NumberOption =
        values().find { it.displayName == displayName } ?: NONE
  }
}