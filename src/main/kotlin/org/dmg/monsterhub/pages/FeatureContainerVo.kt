package org.dmg.monsterhub.pages

import org.dmg.monsterhub.data.FeatureContainerData
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.pages.ItemType.*
import java.math.BigDecimal

class FeatureContainerVo(
    private val data: FeatureContainerData,
    private val item: FeatureContainerItem?,

    val parent: FeatureContainerVo?
) {
  val type: ItemType = when (item?.onlyOne) {
    null -> CONTAINER
    true -> ONE
    false -> LIST
  }

  val name: String
    get() = when (type) {
      CONTAINER -> featureData?.display() ?: ""
      ONE -> item!!.name + ": " + (featureData?.display() ?: "")
      LIST -> item!!.name
    }

  val rate: BigDecimal?
    get() = when (type) {
      CONTAINER -> featureData?.rate()
      ONE -> featureData?.rate()
      LIST -> data
          .features
          .asSequence()
          .filter { it.feature.featureType == featureType }
          .map { it.rate() }
          .fold(BigDecimal.ZERO) { a, b -> a + b }
    }

  val featureData: FeatureData?
    get() = when (type) {
      CONTAINER -> data.takeIf { it is FeatureData }?.let { it as FeatureData }
      ONE -> data.features.filter { it.feature.featureType == featureType }.singleOrNull()
      LIST -> throw IllegalStateException()
    }

  val featureType: String
    get() = item!!.featureType

  //--------------------------------------------------------------------------------------------------------------------

  val canAdd: Boolean
    get() = when (type) {
      CONTAINER -> false
      ONE -> featureData == null
      LIST -> true
    }

  fun add(new: FeatureData) {
    data.features.add(new)
  }

  val canEdit: Boolean
    get() = when (type) {
      CONTAINER -> data is FeatureData
      ONE -> featureData != null
      LIST -> false
    }

  fun delete(): FeatureContainerVo? = when (type) {
    CONTAINER ->
      parent?.also { parent ->
        featureData?.also {
          parent.data.features.remove(it)
        }
      } ?: run {
        featureData?.also {
          data.features.remove(it)
        }
        null
      }
    ONE ->
      featureData?.let {
        data.features.remove(it)
        null
      }
    LIST -> throw IllegalStateException()
  }

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
          .map { FeatureContainerVo(it, null, this) }
          .toList()
      else -> featureData
          ?.feature
          ?.containFeatureTypes
          ?.map { FeatureContainerVo(featureData!!, it, this) }
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