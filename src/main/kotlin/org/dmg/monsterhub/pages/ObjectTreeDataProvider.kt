package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.service.SettingObjectDataProvider
import org.springframework.stereotype.Service
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
) : AbstractBackEndHierarchicalDataProvider<SettingObject, String>() {

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
    null, is Folder -> dataProviders.any { it.hasChildrenAlikeBySetting(settingObject as Folder?, setting) }
    else -> false
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<SettingObject, String>?) =
      query?.parent?.let { it as Folder }
          .let { parent ->
            val search = query?.filter?.orElse("") ?: ""
            dataProviders
                .flatMap { it.getChildrenAlikeBySetting(parent, search, setting) }
          }
          .stream()

  override fun getChildCount(query: HierarchicalQuery<SettingObject, String>?) =
      query?.parent?.let { it as Folder }
          .let { parent ->
            val search = query?.filter?.orElse("") ?: ""
            dataProviders.sumBy { it.countChildrenAlikeBySetting(parent, search, setting) }
          }

  @Deprecated(message = "")
  fun children(parent: SettingObject?): List<SettingObject>? = when (parent) {
    null -> children[null]
    is Folder -> children[parent]
    else -> emptyList()
  }

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

  fun update(settingObject: SettingObject): SettingObject = action(settingObject) {
    it
        .save(settingObject)
        .also {
          if (!settingObject.hidden) {
            replaceWithRefreshed(settingObject, it)
            refreshItem(it)
          }
        }
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

  fun reread(settingObject: SettingObject) {
    action(settingObject) {
      it
          .refresh(settingObject)
          .also { replaceWithRefreshed(settingObject, it) }
    }
  }

  private fun replaceWithRefreshed(settingObject: SettingObject, refreshed: SettingObject) {
    children[settingObject.parent]?.let { list ->
      list.remove(settingObject)
      list.add(refreshed)
      list.sortWith(COMPARATOR)
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