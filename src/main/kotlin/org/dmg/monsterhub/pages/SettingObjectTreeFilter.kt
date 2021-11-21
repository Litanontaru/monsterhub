package org.dmg.monsterhub.pages

import org.dmg.monsterhub.data.setting.SettingObject

data class SettingObjectTreeFilter(
    private val filterValue: String? = null,
    private val findUsages: SettingObject? = null
) {
  fun filterValue() = filterValue!!

  fun findUsages() = findUsages!!

  fun hasFilter() = filterValue?.isNotBlank() ?: false

  fun hasFindUsages() = findUsages != null

  fun isEmpty() = filterValue == null && findUsages == null

  companion object {
    val EMPTY_FILTER = SettingObjectTreeFilter()
  }
}