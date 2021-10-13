package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.Feature
import javax.persistence.Entity

@Entity
class Faction : Feature() {
  var tier: Int = 0

  companion object {
    val FACTION = "FACTION"
  }
}