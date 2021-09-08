package org.dmg.monsterhub.data

import javax.persistence.Entity

@Entity
class Armor: Item() {
  var strong: Int = 0
  var standard: Int = 0
  var weak: Int = 0


  companion object {
    val ARMOR = "ARMOR"
  }
}