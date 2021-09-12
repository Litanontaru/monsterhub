package org.dmg.monsterhub.data.meta

import org.dmg.monsterhub.data.DBObject
import javax.persistence.Entity

@Entity
class FeatureContainerItem : DBObject() {
  lateinit var featureType: String

  var name: String = ""

  var onlyOne: Boolean = false
}