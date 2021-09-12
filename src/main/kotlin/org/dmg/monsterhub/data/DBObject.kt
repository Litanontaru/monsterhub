package org.dmg.monsterhub.data

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

@MappedSuperclass
open class DBObject {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open var id: Long = 0

  @Transient
  var deleteOnly: Boolean = false
}