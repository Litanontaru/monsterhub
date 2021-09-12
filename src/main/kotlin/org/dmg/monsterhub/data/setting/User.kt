package org.dmg.monsterhub.data.setting

import org.dmg.monsterhub.data.DBObject
import javax.persistence.Entity

@Entity
class User : DBObject() {
  var name: String = ""
}