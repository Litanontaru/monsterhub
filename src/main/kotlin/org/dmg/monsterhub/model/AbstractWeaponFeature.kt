package org.dmg.monsterhub.model

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class AbstractWeaponFeature : Detailed {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

  var feature: String = ""
  var primaryNumber: Int = 0
  var secondaryNumber: Int = 0
  override var details: String = ""
}