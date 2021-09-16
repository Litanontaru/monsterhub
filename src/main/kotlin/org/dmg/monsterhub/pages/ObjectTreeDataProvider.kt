package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.service.SettingObjectDataProvider
import org.springframework.stereotype.Service
import java.util.stream.Stream
import javax.transaction.Transactional

@Transactional
@Service
class ObjectTreeDataProviderService(
    private val dataProviders: List<SettingObjectDataProvider>
) {
  operator fun invoke(setting: Setting): ObjectTreeDataProvider = ObjectTreeDataProvider(setting, dataProviders)
}

class ObjectTreeDataProvider(
    private val setting: Setting,
    private val dataProviders: List<SettingObjectDataProvider>
) : AbstractBackEndHierarchicalDataProvider<SettingObject, Unit>() {

  var children = mutableMapOf<Folder?, MutableList<SettingObject>>()
  var onAdd: ((SettingObject) -> Unit)? = null

  init {
    dataProviders
        .flatMap { it.getAllBySetting(setting) }
        .groupBy { it.parent }
        .mapValues { it.value.asSequence().sortedWith(COMPARATOR).toMutableList() }
        .also { children.putAll(it) }
  }

  override fun hasChildren(settingObject: SettingObject?) = when (settingObject) {
    null -> false
    is Folder -> children[settingObject]?.isNotEmpty() ?: false
    else -> false
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<SettingObject, Unit>?) =
      children(query?.parent)?.stream() ?: Stream.empty()

  override fun getChildCount(query: HierarchicalQuery<SettingObject, Unit>?) =
      children(query?.parent)?.size ?: 0

  fun children(parent: SettingObject?): List<SettingObject>? = when (parent) {
    null -> children[null]
    is Folder -> children[parent]
    else -> emptyList()
  }

  fun roots() = children[null] ?: listOf<SettingObject>()

  fun add(newSettingObject: SettingObject) {
    action(newSettingObject) {
      newSettingObject.setting = setting
      val savedObject = it.refresh(it.save(newSettingObject))
      val list = children.getOrPut(savedObject.parent) { mutableListOf() }
      list += savedObject
      list.sortWith(COMPARATOR)

      if (savedObject.parent != null) {
        refreshItem(savedObject.parent, true)
        if (onAdd != null) {
          onAdd!!(savedObject)
        }
      } else {
        refreshAll()
      }

      savedObject
    }
  }

  fun update(settingObject: SettingObject): SettingObject =
      action(settingObject) {
        it.save(settingObject)
            .also { refreshItem(it) }
      }

  fun move(settingObject: SettingObject, new: Folder?) {
    action(settingObject) {
      val old = settingObject.parent
      children[old]?.also {
        it -= settingObject
      }
      settingObject.parent = new
      val list = children.getOrPut(settingObject.parent) { mutableListOf() }
      list += settingObject
      list.sortWith(COMPARATOR)

      it.save(settingObject).also {
        refreshItem(old, true)
        refreshItem(new, true)
      }
    }
  }

  fun delete(settingObject: SettingObject) {
    action(settingObject) {
      children[settingObject.parent]?.also {
        it -= settingObject
      }

      settingObject.deleteOnly = true
      it.save(settingObject).also {
        if (it.parent != null) {
          refreshItem(it.parent, true)
        } else {
          refreshAll()
        }
      }
    }
  }

  private fun action(settingObject: SettingObject, block: (SettingObjectDataProvider) -> SettingObject) =
      dataProviders
          .first { settingObject::class.java.isAssignableFrom(it.objectClass) }
          .let(block)

  fun dataProviders(): Sequence<SettingObjectDataProvider> = dataProviders.asSequence()

  fun find(objId: Long): SettingObject? =
      children.values.asSequence().flatMap { it.asSequence() }.find { it.id == objId }

  companion object {
    private val COMPARATOR = compareBy<SettingObject>({ it !is Folder }, { it.name })
  }
}