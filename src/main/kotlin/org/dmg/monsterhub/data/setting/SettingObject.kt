package org.dmg.monsterhub.data.setting

import javax.persistence.*

@MappedSuperclass
open class SettingObject {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open var id: Long = 0

  open var name: String = ""

  @ManyToOne
  @JoinColumn(name = "setting_id", nullable = true)
  open lateinit var setting: Setting

  @ManyToOne
  @JoinColumn(name = "parent_id", nullable = true)
  open var parent: Folder? = null

  override fun toString(): String = "${this.javaClass.simpleName}($name)"
}