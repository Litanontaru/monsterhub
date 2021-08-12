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

  init {
    dataProviders
        .flatMap { it.getAllBySetting(setting) }
        .groupBy { it.parent }
        .mapValues { it.value.toMutableList() }
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

  fun add(settingObject: SettingObject) {
    action(settingObject) {
      settingObject.setting = setting
      it.save(settingObject)
      children.getOrPut(settingObject.parent) { mutableListOf() } += settingObject
      refreshAll()
    }
  }

  fun update(settingObject: SettingObject) {
    action(settingObject) {
      it.save(settingObject)

      refreshItem(settingObject)
    }
  }

  fun move(settingObject: SettingObject, new: Folder?) {
    action(settingObject) {
      it.save(settingObject)

      children[settingObject.parent]?.also {
        it -= settingObject
      }
      settingObject.parent = new
      children.getOrPut(settingObject.parent) { mutableListOf() } += settingObject
      refreshAll()
    }
  }

  fun delete(settingObject: SettingObject) {
    action(settingObject) {
      it.delete(settingObject)

      children[settingObject.parent]?.also {
        it -= settingObject
      }
      refreshAll()
    }
  }

  private fun action(settingObject: SettingObject, block: (SettingObjectDataProvider) -> Unit) {
    dataProviders
        .first { settingObject::class.java.isAssignableFrom(it.objectClass) }
        .also(block)
  }

  fun dataProviders(): Sequence<SettingObjectDataProvider> = dataProviders.asSequence()
}