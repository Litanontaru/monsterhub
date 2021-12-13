package org.dmg.monsterhub.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class TreeObjectController {
  private lateinit var service: TreeObjectService

  @Autowired
  fun setService(@Lazy service: TreeObjectService) {
    this.service = service
  }

  fun get(id: Long): TreeObject = service.get(id)

  fun setNumber(id: Long, index: Int, value: BigDecimal?) {
    service.setNumber(id, index, value)
  }

  fun setDesignation(id: Long, key: String, value: String?) {
    service.setDesignation(id, key, value)
  }

  fun addFeatureDataFromFeatureData(id: Long, featureId: Long) =
      service.addFeatureDataFromFeatureData(id, featureId)

  fun addFeatureDataFromContainerData(id: Long, featureId: Long) =
      service.addFeatureDataFromContainerData(id, featureId)

  fun removeFeatureDataFromFeatureData(id: Long, dataId: Long) {
    service.removeFeatureDataFromFeatureData(id, dataId)
  }

  fun removeFeatureDataFromContainerData(id: Long, dataId: Long) {
    service.removeFeatureDataFromContainerData(id, dataId)
  }

  fun replaceFeatureDataFromFeatureData(id: Long, featureId: Long) =
      service.replaceFeatureDataFromFeatureData(id, featureId)

  fun replaceFeatureDataFromContainerData(id: Long, featureId: Long) =
      service.replaceFeatureDataFromContainerData(id, featureId)

  fun addBaseCreature(baseId: Long, creatureId: Long): TreeObject =
      service.addBaseCreature(baseId, creatureId)

  fun removeBaseCreature(baseId: Long, creatureId: Long) {
    service.removeBaseCreature(baseId, creatureId)
  }

  fun setFeatureName(id: Long, name: String) {
    service.setFeatureName(id, name)
  }
}