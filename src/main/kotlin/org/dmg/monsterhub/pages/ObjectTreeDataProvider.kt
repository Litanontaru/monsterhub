package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.setting.Folder
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.service.ObjectManagerService
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
) : AbstractBackEndHierarchicalDataProvider<SettingObject, String>(), ObjectManagerService {

  var onAdd: ((SettingObject) -> Unit)? = null

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
                .sortedWith(COMPARATOR)
          }
          .stream()

  override fun getChildCount(query: HierarchicalQuery<SettingObject, String>?) =
      query?.parent?.let { it as Folder }
          .let { parent ->
            val search = query?.filter?.orElse("") ?: ""
            dataProviders.sumBy { it.countChildrenAlikeBySetting(parent, search, setting) }
          }

  fun firstFolder(parent: Folder?, search: String): Folder? =
      dataProviders
          .first { it.supportType("FOLDER") }
          .getChildrenAlikeBySetting(parent, "", setting)
          .filter { it.name == search }
          .take(1)
          .map { it as Folder }
          .singleOrNull()

  fun add(newSettingObject: SettingObject) {
    action(newSettingObject) {
      newSettingObject.setting = setting
      val savedObject = it.refresh(it.save(newSettingObject))

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

  override fun update(settingObject: SettingObject): SettingObject = action(settingObject) {
    it
        .save(settingObject)
        .also {
          if (!settingObject.hidden) {
            refreshItem(it)
          }
        }
  }

  fun move(settingObject: SettingObject, new: Folder?) {
    action(settingObject) {
      val old = settingObject.parent
      settingObject.parent = new

      it.save(settingObject).also {
        if (old == null || new == null) {
          refreshAll()
        } else {
          refreshItem(old, true)
          refreshItem(new, true)
        }
      }
    }
  }

  fun delete(settingObject: SettingObject) {
    action(settingObject) {
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
          .also { refreshItem(it, true) }
    }
  }

  private fun action(settingObject: SettingObject, block: (SettingObjectDataProvider) -> SettingObject) =
      dataProviders
          .first { settingObject::class.java.isAssignableFrom(it.objectClass) }
          .let(block)

  fun dataProviders(): Sequence<SettingObjectDataProvider> = dataProviders.asSequence()

  fun find(objId: Long): SettingObject? =
      dataProviders
          .map { it.getById(objId) }
          .find { it != null }

  override fun create(featureType: String): SettingObject =
      dataProviders()
          .first { it.supportType(featureType) }
          .create()

  companion object {
    private val COMPARATOR = compareBy<SettingObject>({ it !is Folder }, { it.name })
  }
}