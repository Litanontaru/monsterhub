package org.dmg.monsterhub.data.setting

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  var id: Long = 0

  var name: String = ""
}