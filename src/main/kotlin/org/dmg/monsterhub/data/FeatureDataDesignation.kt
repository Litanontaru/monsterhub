package org.dmg.monsterhub.data

import org.hibernate.annotations.Type
import javax.persistence.Entity

@Entity
class FeatureDataDesignation: DBObject() {
  var designationKey: String = ""

  @Type(type = "text")
  var value: String = ""
}