package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.service.FeatureService
import java.util.stream.Stream

class ObjectTreeDataProvider2(
    private val featureService: FeatureService
) : AbstractBackEndHierarchicalDataProvider<SettingObjectTreeNode, Unit>() {
  var setting: Setting? = null

  var filter: String = ""
    set(value) {
      field = value
      refreshAll()
    }

  override fun hasChildren(item: SettingObjectTreeNode?): Boolean {
    return setting
        ?.let { setting ->
          when {
            item == null -> true
            item.featureType != "FOLDER" -> false
            else -> featureService.exists(setting, item.name)
          }
        }
        ?: false
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<SettingObjectTreeNode, Unit>?): Stream<SettingObjectTreeNode> {
    return setting
        ?.let { setting ->
          val item = query?.parent
          val folder = item?.name ?: ""

          val folders = childrenFolders(setting, folder).sorted().map { FolderTreeNode(it) }
          val features = featureService
              .features(setting, folder)
              .map { it.toFeature() }
              .sortedBy { it.name }

          val settingIn = if (folder == "") {
            listOf(SettingTreeNode(setting.id, setting.name))
          } else listOf()

          return (folders + features + settingIn).stream()
        }
        ?: Stream.empty()
  }

  override fun getChildCount(query: HierarchicalQuery<SettingObjectTreeNode, Unit>?): Int {
    return setting
        ?.let { setting ->
          val item = query?.parent

          val settingIn = if (item == null) 1 else 0

          val folder = item?.name ?: ""

          val featuresCount = featureService.count(setting, folder)

          return settingIn +
              featuresCount +
              childrenFolders(setting, folder).size
        } ?: 0
  }

  private fun childrenFolders(setting: Setting, folder: String): List<String> {
    val start = if (folder.isNotBlank()) folder.length + 1 else 0
    return featureService.folders(setting, folder + "%")
        .filter { it.isNotBlank() }
        .map {
          it.indexOf('.', start)
              .let { index ->
                if (index >= 0) it.substring(0, index + 1)
                else ""
              }
        }
        .filter { it.isNotBlank() }
        .distinct()
  }

  fun hide(obj: SettingObjectTreeNode) {
    if (obj is FeatureTreeNode) {
      featureService.hide(obj.id)
      //todo поддержать полное обновление, если удалён последний элемент папки
      refreshItem(FolderTreeNode(obj.folder), true)
    }
  }
}

interface SettingObjectTreeNode {
  val id: Long
  val name: String
  val featureType: String
  val folder: String
}

data class FeatureTreeNode(
    override val id: Long,
    override val name: String,
    override val featureType: String,
    override val folder: String
) : SettingObjectTreeNode

data class FolderTreeNode(
    override val name: String
) : SettingObjectTreeNode {
  override val id: Long
    get() = 0

  override val featureType: String
    get() = "FOLDER"

  override val folder: String
    get() = name.substring(0, name.length - 1).let { it.substring(0, it.lastIndexOf('.') + 1) }
}

class SettingTreeNode(
    override val id: Long,
    override val name: String
) : SettingObjectTreeNode {
  override val featureType: String
    get() = "SETTING"

  override val folder: String
    get() = ""
}

fun SettingObjectTreeNode.toFeature() = FeatureTreeNode(id, name, featureType, folder)

fun SettingObjectTreeNode.isFolder() =
    this is FolderTreeNode

fun SettingObjectTreeNode.folderName() =
    name.substring(0, name.length - 1).let { it.substring(it.lastIndexOf('.') + 1) }