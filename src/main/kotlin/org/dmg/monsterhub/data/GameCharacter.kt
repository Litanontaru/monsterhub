package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.Faction.Companion.FACTION
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import javax.persistence.Entity

@Entity
class GameCharacter : Creature() {
  val frazzle: Int = 0

  val traumaOne: Boolean = false
  val traumaTwo: Boolean = false
  val traumaThree: Boolean = false

  override fun meta() = CHARACTER_META

  companion object {
    val CHARACTER = "CHARACTER"

    val CHARACTER_META = META + listOf(
        FeatureContainerItem().apply {
          name = "Фракция"
          featureType = FACTION
          onlyOne = true
        }
    )
  }
}