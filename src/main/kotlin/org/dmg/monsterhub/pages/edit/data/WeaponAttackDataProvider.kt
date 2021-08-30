package org.dmg.monsterhub.pages.edit.data

import com.vaadin.flow.data.provider.AbstractDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.Weapon
import org.dmg.monsterhub.data.WeaponAttack
import java.util.stream.Stream

class WeaponAttackDataProvider(
    val weapon: Weapon,
    val update: (Any) -> Unit
) : AbstractDataProvider<WeaponAttack, Unit>(), DataProvider<WeaponAttack, Unit> {
  override fun isInMemory(): Boolean = true

  override fun fetch(query: Query<WeaponAttack, Unit>?): Stream<WeaponAttack> =
      weapon.attacks.stream()
          .skip(query?.offset?.toLong() ?: 0)
          .limit(query?.limit?.toLong() ?: 0)

  override fun size(query: Query<WeaponAttack, Unit>?): Int = weapon.attacks.size

  fun add(obj: WeaponAttack) {
    weapon.attacks.add(obj)
    update(weapon)
    update(obj)
    refreshAll()
  }

  fun delete(obj: WeaponAttack) {
    weapon.attacks.remove(obj)
    update(weapon)
    obj.deleteOnly = true
    update(obj)
    refreshAll()
  }

  fun modify(obj: WeaponAttack) {
    update(obj)
    refreshItem(obj)
  }
}