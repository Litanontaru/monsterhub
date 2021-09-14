package org.dmg.monsterhub.data

import org.dmg.monsterhub.data.meta.FeatureContainerItem

interface FeatureContainerData {
  var features: MutableList<FeatureData>

  fun meta(): List<FeatureContainerItem>
}