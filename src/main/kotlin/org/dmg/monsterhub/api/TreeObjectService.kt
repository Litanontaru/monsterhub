package org.dmg.monsterhub.api

import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.repository.*
import org.dmg.monsterhub.service.Decimal
import org.dmg.monsterhub.service.sum
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TreeObjectService(
  private val controller: TreeObjectController,

  private val containerDataRepository: ContainerDataRepository,
  private val creatureRepository: CreatureRepository,
  private val featureDataRepository: FeatureDataRepository,
  private val featureRepository: FeatureRepository,
  private val featureDataDesignationRepository: FeatureDataDesignationRepository
) {
  //--------------------------------------------------------------------------------------------------------------------

  fun get(id: Long) = containerDataRepository.getById(id).toTreeObject(TreeObjectType.FEATURE_OBJECT)

  fun ContainerData.toTreeObject(type: TreeObjectType) = TreeObject(
    id,
    "",
    rate(),
    { rate() },
    id,
    type,
    getAttributes(
      get = { featureDataRepository.findAllByContainerData_IdAndFeature_FeatureType(id, it) },
      add = { obj -> controller.addFeatureDataFromContainerData(id, obj.id) },
      remove = { obj -> controller.removeFeatureDataFromContainerData(id, obj.id) },
      replace = { obj -> controller.replaceFeatureDataFromContainerData(id, obj.id) }
    ),
    primitive = mutableListOf(name),
    setPrimitive = { _, value -> controller.setFeatureName(id, (value as String?) ?: "") }
  )

  private fun FeatureData.toTreeObject() = TreeObject(
    id,
    if (feature is ContainerData) "" else feature.name,
    rate(),
    { rate() },
    feature.id,
    TreeObjectType.FEATURE_DATA,
    getFeatureAttributes()
  )

  fun ContainerData.toTreeObjectOption() = TreeObjectOption(
    id,
    name,
    Decimal.NONE.toString()
  )

  //--------------------------------------------------------------------------------------------------------------------

  fun setNumber(id: Long, index: Int, value: BigDecimal?) {
    val data = featureDataRepository.getById(id)
    when (index) {
      0 -> data.x = value as BigDecimal
      1 -> data.xa = value as BigDecimal
      2 -> data.xb = value as BigDecimal
      3 -> data.y = value as BigDecimal
      4 -> data.ya = value as BigDecimal
      5 -> data.yb = value as BigDecimal
      6 -> data.z = value as BigDecimal
      7 -> data.za = value as BigDecimal
      8 -> data.zb = value as BigDecimal
    }
  }

  fun setDesignation(id: Long, key: String, value: String?) {
    val data = featureDataRepository.getById(id)
    when (value) {
      null -> data.designations.removeIf { it.designationKey == key }
      else -> data.designations.find { it.designationKey == key }?.also { it.value = value }
        ?: run {
          FeatureDataDesignation().also {
            it.value = value
            it.designationKey = key
            featureDataDesignationRepository.save(it)
            data.designations.add(it)
          }
        }
    }
  }

  fun addFeatureDataFromFeatureData(id: Long, featureId: Long) =
    add(featureId, featureDataRepository.getById(id))

  fun addFeatureDataFromContainerData(id: Long, featureId: Long) =
    add(featureId, containerDataRepository.getById(id))

  private fun add(featureId: Long, data: FeatureContainerData): TreeObject {
    val feature = featureRepository.getById(featureId)
    return FeatureData()
      .also {
        it.feature = feature
        data.features.add(it)
        featureDataRepository.save(it)
      }
      .toTreeObject()
  }

  fun removeFeatureDataFromFeatureData(id: Long, dataId: Long) {
    val data = featureDataRepository.getById(id)
    data.features.removeIf { it.id == dataId }
  }

  fun removeFeatureDataFromContainerData(id: Long, dataId: Long) {
    val data = containerDataRepository.getById(id)
    data.features.removeIf { it.id == dataId }
  }

  fun replaceFeatureDataFromFeatureData(id: Long, featureId: Long) =
    replace(featureId, featureDataRepository.getById(id))

  fun replaceFeatureDataFromContainerData(id: Long, featureId: Long) =
    replace(featureId, containerDataRepository.getById(id))

  private fun replace(featureId: Long, data: FeatureContainerData): TreeObject {
    val feature = featureRepository.getById(featureId)
    data.features.removeIf { it.feature.featureType == feature.featureType }
    return FeatureData()
      .also { it.feature = feature }
      .also { data.features.add(it) }
      .toTreeObject()
  }

  fun addBaseCreature(baseId: Long, creatureId: Long): TreeObject {
    val base = creatureRepository.getById(baseId)
    creatureRepository.getById(creatureId).base.add(base)
    return base.toTreeObject(TreeObjectType.BASE_CREATURE)
  }

  fun removeBaseCreature(baseId: Long, creatureId: Long) {
    creatureRepository.getById(creatureId).base.removeIf { it.id == baseId }
  }

  fun setFeatureName(id: Long, name: String) {
    containerDataRepository.getById(id).name = name
  }

  //--------------------------------------------------------------------------------------------------------------------

  private fun FeatureData.getFeatureAttributes(): List<TreeObjectAttribute> {
    val list = mutableListOf<TreeObjectAttribute>()
    if (feature is ContainerData) {
      list += TreeObjectAttribute(
        name = "",
        type = TreeObjectType.FEATURE,
        get = { listOf(controller.get(feature.id)) }
      )
    }

    if (feature.x != NumberOption.NONE) {
      list += TreeObjectAttribute(
        name = "X",
        type = feature.x.toAttributeType()!!,
        primitive = mutableListOf(x, xa, xb),
        setPrimitive = { index, value -> controller.setNumber(id, index, value as BigDecimal?) }
      )
    }
    if (feature.y != NumberOption.NONE) {
      list += TreeObjectAttribute(
        name = "Y",
        type = feature.y.toAttributeType()!!,
        primitive = mutableListOf(y, ya, yb),
        setPrimitive = { index, value -> controller.setNumber(id, 3 + index, value as BigDecimal?) }
      )
    }
    if (feature.z != NumberOption.NONE) {
      list += TreeObjectAttribute(
        name = "Z",
        type = feature.z.toAttributeType()!!,
        primitive = mutableListOf(z, za, zb),
        setPrimitive = { index, value -> controller.setNumber(id, 6 + index, value as BigDecimal?) }
      )
    }
    feature.designations.forEach { key ->
      val trimmedKey = key.substringBeforeLast("*")
      list += TreeObjectAttribute(
        name = trimmedKey,
        type = TreeObjectType.LINE,
        primitive = mutableListOf(designations.find { it.designationKey == trimmedKey }?.value),
        setPrimitive = { _, value -> controller.setDesignation(id, trimmedKey, value as String?) }
      )
    }
    return list + getFeatureContainerDataAttributes(
      get = { featureDataRepository.findAllByMainFeature_IdAndFeature_FeatureType(id, it) },
      add = { obj -> controller.addFeatureDataFromFeatureData(id, obj.id) },
      remove = { obj -> controller.removeFeatureDataFromFeatureData(id, obj.id) },
      replace = { obj -> controller.replaceFeatureDataFromFeatureData(id, obj.id) }
    )
  }

  private fun FeatureContainerData.getFeatureContainerDataAttributes(
    get: (String) -> List<FeatureData>,
    add: (TreeObjectOption) -> TreeObject,
    remove: (TreeObject) -> Unit,
    replace: (TreeObjectOption) -> TreeObject
  ): List<TreeObjectAttribute> {
    return meta().map {
      val preload = get(it.featureType).map { it.toTreeObject() }.toMutableList()
      TreeObjectAttribute(
        name = it.name,
        type = if (it.onlyOne) TreeObjectType.SINGLE_REF else TreeObjectType.MULTIPLE_REF,
        get = { preload },
        isEmpty = { preload.isEmpty() },
        dictionary = listOf(it.featureType),
        canCreate = it.allowHidden,
        add = add,
        remove = remove,
        replace = replace,
        rate = this.features.filter { f -> f.feature.featureType == it.featureType }.map { it.rate() }.sum(),
        read = { get(it.featureType).map { it.rate() }.sum() }
      )
    }
  }

  private fun ContainerData.getAttributes(
    get: (String) -> List<FeatureData>,
    add: (TreeObjectOption) -> TreeObject,
    remove: (TreeObject) -> Unit,
    replace: (TreeObjectOption) -> TreeObject
  ): List<TreeObjectAttribute> {
    val base = if (Creature.CREATURE_TYPES.contains(featureType)) {
      creatureRepository.getById(id).getBaseCreatures()
    } else {
      listOf()
    }
    return base + getFeatureContainerDataAttributes(get, add, remove, replace)
  }

  private fun Creature.getBaseCreatures(): List<TreeObjectAttribute> {
    return listOf(TreeObjectAttribute(
      name = "Основа",
      type = TreeObjectType.MULTIPLE_REF,
      get = { this.base.map { it.toTreeObject(TreeObjectType.BASE_CREATURE) } },
      isEmpty = { this.base.isEmpty() },
      dictionary = listOf(Creature.CREATURE_RACE_TEMPLATE, Creature.CREATURE_RACE),
      add = { obj -> controller.addBaseCreature(obj.id, id) },
      remove = { obj -> controller.removeBaseCreature(obj.id, id) }
    ))
  }
}