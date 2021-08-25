package org.dmg.monsterhub.pages

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.pages.ItemType.*

class FeatureContainerVo(
    private val data: FeatureContainerData,
    private val item: FeatureContainerItem?
) {
  val type: ItemType = when (item?.onlyOne) {
    null -> CONTAINER
    true -> ONE
    false -> LIST
  }


  val name: String
    get() = when (type) {
      CONTAINER -> featureData?.display() ?: ""
      ONE -> item!!.name + ": " + (featureData?.display() ?: " - ")
      LIST -> item!!.name
    }

  val featureData: FeatureData?
    get() = when (type) {
      CONTAINER -> data as FeatureData
      ONE -> data.features.filter { it.feature.featureType == featureType }.singleOrNull()
      LIST -> throw IllegalStateException()
    }

  val featureType: String
      get() = item!!.featureType

  //--------------------------------------------------------------------------------------------------------------------

  val hasChildren: Boolean
    get() = when (type) {
      LIST -> data.features.any { it.feature.featureType == featureType }
      else -> featureData?.features?.isNotEmpty() ?: false
    }

  val children: List<FeatureContainerVo>
    get() = when (type) {
      LIST -> data
          .features
          .asSequence()
          .filter { it.feature.featureType == featureType }
          .map { FeatureContainerVo(it, null) }
          .toList()
      else -> featureData
          ?.feature
          ?.containFeatureTypes
          ?.map { FeatureContainerVo(featureData!!, it) }
          ?: emptyList()
    }

  val count: Int
    get() = when (type) {
      LIST -> data
          .features
          .asSequence()
          .filter { it.feature.featureType == featureType }
          .count()
      else -> featureData?.feature?.containFeatureTypes?.size ?: 0
    }
}

enum class ItemType {
  CONTAINER,
  ONE,
  LIST
}