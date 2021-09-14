package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.FeatureContainerItem
import javax.persistence.Entity

@Entity
class Armor: Item() {
  var strong: Int = 0
  var standard: Int = 0
  var weak: Int = 0

  override fun meta(): List<FeatureContainerItem> = META

  companion object {
    val ARMOR = "ARMOR"

    val META = listOf(
        FeatureContainerItem().apply {
          name = "Свойства"
          featureType = "ARMOR_FEATURE"
        }
    )
  }
}