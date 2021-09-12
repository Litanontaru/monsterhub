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
        .mapValues { it.value.asSequence().sortedWith(compareBy({ it !is Folder }, { it.name })).toMutableList() }
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

  private fun children(parent: SettingObject?): List<SettingObject>? = when (parent) {
    null -> children[null]
    is Folder -> children[parent]
    else -> emptyList()
  }

  fun roots() = children[null] ?: listOf<SettingObject>()

  fun add(newSettingObject: SettingObject) {
    action(newSettingObject) {
      newSettingObject.setting = setting
      it.save(newSettingObject) { savedObject ->
        val savedSettingObject = it.refresh(savedObject)
        children.getOrPut(savedSettingObject.parent) { mutableListOf() } += savedSettingObject

        if (savedObject.parent != null) {
          refreshItem(savedObject.parent, true)
          if (onAdd != null) {
            onAdd!!(savedSettingObject)
          }
        } else {
          refreshAll()
        }
      }
    }
  }

  fun update(settingObject: SettingObject) {
    action(settingObject) {
      it.save(settingObject) { refreshItem(it) }
    }
  }

  fun move(settingObject: SettingObject, new: Folder?) {
    action(settingObject) {
      children[settingObject.parent]?.also {
        it -= settingObject
      }
      settingObject.parent = new
      children.getOrPut(settingObject.parent) { mutableListOf() } += settingObject

      it.save(settingObject) {
        refreshAll()
      }
    }
  }

  fun delete(settingObject: SettingObject) {
    action(settingObject) {
      children[settingObject.parent]?.also {
        it -= settingObject
      }

      settingObject.deleteOnly = true
      it.save(settingObject) {
        if (settingObject.parent != null) {
          refreshItem(settingObject.parent, true)
        } else {
          refreshAll()
        }
      }
    }
  }

  private fun action(settingObject: SettingObject, block: (SettingObjectDataProvider) -> Unit) {
    dataProviders
        .first { settingObject::class.java.isAssignableFrom(it.objectClass) }
        .also(block)
  }

  fun dataProviders(): Sequence<SettingObjectDataProvider> = dataProviders.asSequence()
}