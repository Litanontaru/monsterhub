package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery
import org.dmg.monsterhub.data.setting.Setting
import org.dmg.monsterhub.repository.FeatureRepository
import org.springframework.stereotype.Service
import java.util.stream.Stream

@Service
class ObjectTreeDataProviderService2(
    private val featureRepository: FeatureRepository
) {
  operator fun invoke(setting: Setting): ObjectTreeDataProvider2 = ObjectTreeDataProvider2(setting, featureRepository)
}

class ObjectTreeDataProvider2(
    private val setting: Setting,
    private val featureRepository: FeatureRepository
) : AbstractBackEndHierarchicalDataProvider<SettingObjectTreeNode, Unit>() {
  var filter: String = ""
    set(value) {
      field = value
      refreshAll()
    }

  override fun hasChildren(item: SettingObjectTreeNode?): Boolean {
    if (item == null) {
      return true
    }
    if (item.featureType != "FOLDER") {
      return false
    }
    return featureRepository.existsFeatureBySettingAndFolderStartingWith(setting, item.name)
  }

  override fun fetchChildrenFromBackEnd(query: HierarchicalQuery<SettingObjectTreeNode, Unit>?): Stream<SettingObjectTreeNode> {
    val item = query?.parent
    val folder = item?.name ?: ""

    val folders = childrenFolders(folder).sorted().map { FolderTreeNode(it) }
    val features = featureRepository
        .featureBySettingAndFolder(setting, folder)
        .sortedBy { it.name }

    val settingIn = if (folder == "") {
      listOf(SettingTreeNode(setting.name))
    } else listOf()

    return (folders + features + settingIn).stream()
  }

  override fun getChildCount(query: HierarchicalQuery<SettingObjectTreeNode, Unit>?): Int {
    val item = query?.parent

    val settingIn = if (item == null) 1 else 0

    val folder = item?.name ?: ""

    val featuresCount = featureRepository.countFeatureBySettingAndFolderAndHiddenFalse(setting, folder)

    return settingIn +
        featuresCount +
        childrenFolders(folder).size
  }

  private fun childrenFolders(folder: String): List<String> {
    val start = if (folder.isNotBlank()) folder.length + 1 else 0
    return featureRepository.foldersBySettingAndFolderStartingWithAndHiddenFalse(setting, folder + "%")
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
}

interface SettingObjectTreeNode {
  val name: String
  val featureType: String
}

class FolderTreeNode(
    override val name: String
) : SettingObjectTreeNode {
  override val featureType: String
    get() = "FOLDER"
}

class SettingTreeNode(
    override val name: String
) : SettingObjectTreeNode {
  override val featureType: String
    get() = "SETTING"
}

fun SettingObjectTreeNode.isFolder() =
    this is FolderTreeNode

fun SettingObjectTreeNode.folderName() =
    name.substring(0, name.length - 1).let { it.substring(it.lastIndexOf('.') + 1) }