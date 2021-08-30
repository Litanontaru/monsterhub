package org.dmg.monsterhub.data

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
class Weapon: Item() {
  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "weapon_id")
  var attacks: MutableList<WeaponAttack> = mutableListOf()
}