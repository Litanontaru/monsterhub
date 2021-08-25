package org.dmg.monsterhub.pages

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.meta.FeatureContainerItem

class FeatureContainerVo(
    private val data: FeatureContainerData,
    private val item: FeatureContainerItem?
) {
  val name: String
    get() = when (item?.onlyOne) {
      null -> featureData?.display() ?: ""
      true -> item.name + ": " + (featureData?.display() ?: " - ")
      false -> item.name
    }

  val featureData: FeatureData?
    get() = when (item?.onlyOne) {
      null -> data as FeatureData
      true -> data.features.filter { it.feature.featureType == item.featureType }.singleOrNull()
      false -> throw IllegalStateException()
    }

  val hasChildren: Boolean
    get() = when (item?.onlyOne) {
      false -> data.features.any { it.feature.featureType == item.featureType }
      else -> featureData?.features?.isNotEmpty() ?: false
    }

  val children: List<FeatureContainerVo>
    get() = when (item?.onlyOne) {
      false -> data
          .features
          .asSequence()
          .filter { it.feature.featureType == item.featureType }
          .map { FeatureContainerVo(it, null) }
          .toList()
      else -> featureData
          ?.feature
          ?.containFeatureTypes
          ?.map { FeatureContainerVo(featureData!!, it) }
          ?: emptyList()
    }

  val count: Int
    get() = when (item?.onlyOne) {
      false -> data
          .features
          .asSequence()
          .filter { it.feature.featureType == item.featureType }
          .count()
      else -> featureData?.feature?.containFeatureTypes?.size ?: 0
    }
}
