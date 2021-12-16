package org.dmg.monsterhub.api

import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.service.Decimal

class TreeObject(
    val id: Long,
    val name: String,
    var rate: Decimal,
    val read: () -> Decimal,

    val type: TreeObjectType,
    val attributes: List<TreeObjectAttribute>,
    val primitive: MutableList<Any?> = mutableListOf(),
    val setPrimitive: (Int, Any?) -> Unit = { _, _ -> throw UnsupportedOperationException() }
) {
  fun readRate() = read().also { rate = it }
}

class TreeObjectOption(
    val id: Long,
    val name: String,
    val rate: String
)

class TreeObjectAttribute(
    val name: String,
    val type: TreeObjectType,

    val primitive: MutableList<Any?> = mutableListOf(),
    val setPrimitive: (Int, Any?) -> Unit = { _, _ -> throw UnsupportedOperationException() },

    val get: () -> List<TreeObject> = { throw UnsupportedOperationException() },
    val dictionary: String = "",
    val canCreate: Boolean = false,
    val add: (TreeObjectOption) -> TreeObject = { _ -> throw UnsupportedOperationException() },
    val remove: (TreeObject) -> Unit = { _ -> throw UnsupportedOperationException() },
    val replace: (TreeObjectOption) -> TreeObject = { _ -> throw UnsupportedOperationException() },

    var rate: Decimal = Decimal.NONE,
    val read: () -> Decimal = { Decimal.NONE }
) {
  fun readRate() = read().also { rate = it }
}

enum class TreeObjectType(val terminal: Boolean) {
  POSITIVE(true),
  POSITIVE_AND_INFINITE(true),
  FREE(true),
  DAMAGE(true),
  ARMOR(true),
  IMPORTANCE(true),
  LINE(true),
  MULTILINE(true),
  FEATURE(false),
  SINGLE_REF(false),
  MULTIPLE_REF(false),

  FEATURE_OBJECT(false),
  FEATURE_DATA(false)
}

fun NumberOption.toAttributeType() = when (this) {
  NumberOption.NONE -> null
  NumberOption.POSITIVE -> TreeObjectType.POSITIVE
  NumberOption.POSITIVE_AND_INFINITE -> TreeObjectType.POSITIVE_AND_INFINITE
  NumberOption.FREE -> TreeObjectType.FREE
  NumberOption.DAMAGE -> TreeObjectType.DAMAGE
  NumberOption.ARMOR -> TreeObjectType.ARMOR
  NumberOption.IMPORTANCE -> TreeObjectType.IMPORTANCE
}