package org.dmg.monsterhub.data.setting

import org.dmg.monsterhub.data.Named
import org.dmg.monsterhub.service.Decimal
import javax.persistence.*

@MappedSuperclass
open class SettingObject : Named {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open var id: Long = 0

  override var name: String = ""

  @ManyToOne
  @JoinColumn(name = "setting_id", nullable = true)
  open lateinit var setting: Setting

  @ManyToOne
  @JoinColumn(name = "parent_id", nullable = true)
  open var parent: Folder? = null

  open fun rate() = Decimal.ZERO

  override fun toString(): String = "${this.javaClass.simpleName}($name)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SettingObject) return false
    if (id != other.id) return false
    return true
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }
}