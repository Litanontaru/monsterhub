package org.dmg.monsterhub.api

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.data.Power
import org.dmg.monsterhub.repository.CreatureRepository
import org.dmg.monsterhub.repository.FeatureRepository
import org.dmg.monsterhub.repository.PowerRepository
import org.dmg.monsterhub.repository.SettingRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@Service
@Transactional
class TreeObjectDictionary(
    private val repository: FeatureRepository,
    private val creatureRepository: CreatureRepository,
    private val powerRepository: PowerRepository,
    private val settingRepository: SettingRepository,

    private val treeObjectService: TreeObjectService
) {
  fun dataProvider(featureType: List<String>, settings: List<Long>): TreeObjectOptionDataProvider =
    TreeObjectOptionDataProvider(this, featureType, settings)

  fun getAll(featureType: List<String>, settings: List<Long>, pageable: Pageable): List<TreeObjectOption> =
    repository
      .findAllByFeatureTypeInAndSetting_IdInAndHiddenFalse(featureType, settings, pageable)
      .map { TreeObjectOption(it.id, it.name, it.rate ?: "") }

  fun countAll(featureType: List<String>, settings: List<Long>) =
    repository
      .countAllByFeatureTypeInAndSetting_IdInAndHiddenFalse(featureType, settings)

  fun getAllStarting(
    featureType: List<String>,
    name: String,
    settings: List<Long>,
    pageable: Pageable
  ): List<TreeObjectOption> =
    repository
      .findAllByFeatureTypeInAndNameContainingAndSetting_IdInAndHiddenFalse(featureType, name, settings, pageable)
      .map { TreeObjectOption(it.id, it.name, it.rate ?: "") }

  fun countAllStarting(featureType: List<String>, name: String, settings: List<Long>) =
    repository
      .countAllByFeatureTypeInAndNameContainingAndSetting_IdInAndHiddenFalse(featureType, name, settings)

  fun create(featureType: String, settingId: Long): TreeObjectOption {
    val data = when (featureType) {
      Creature.CREATURE_RACE, Creature.CREATURE_REPRESENTATIVE -> Creature().also {
        it.featureType = featureType
        it.setting = settingRepository.getById(settingId)
        it.hidden = true
        creatureRepository.save(it)
      }
      Power.POWER -> Power().also {
        it.featureType = featureType
        it.setting = settingRepository.getById(settingId)
        it.hidden = true
        powerRepository.save(it)
      }
      else -> throw UnsupportedOperationException()
    }
    return with(treeObjectService) { data.toTreeObjectOption() }
  }
}

class TreeObjectOptionDataProvider(
  val dictionary: TreeObjectDictionary,
  val featureType: List<String>,
  val settings: List<Long>
) : AbstractBackEndDataProvider<TreeObjectOption, Void>() {
  var filter = ""
    set(value) {
      refreshAll()
      field = value
    }

  override fun sizeInBackEnd(query: Query<TreeObjectOption, Void>?): Int {
    return when {
      filter.isNotBlank() -> dictionary.countAllStarting(featureType, filter, settings)
      else -> dictionary.countAll(featureType, settings)
    }
  }

  override fun fetchFromBackEnd(query: Query<TreeObjectOption, Void>?): Stream<TreeObjectOption> {
    return when {
      filter.isNotBlank() -> dictionary
        .getAllStarting(featureType, filter, settings, PageRequest.of(query!!.page, query.pageSize))
        .stream()
      else -> dictionary
        .getAll(featureType, settings, PageRequest.of(query!!.page, query.pageSize))
        .stream()
    }
  }
}