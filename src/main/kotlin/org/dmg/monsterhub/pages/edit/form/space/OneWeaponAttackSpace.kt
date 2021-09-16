package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.WeaponAttack
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import java.math.BigDecimal

object OneWeaponAttackSpace : Space {
  override fun support(obj: Any) = obj is WeaponAttack

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val obj = anyObj as WeaponAttack

    return listOf(
        TextField("Вид").apply {
          value = obj.mode
          addValueChangeListener {
            update(obj) { obj.mode = it.value }
          }
        },
        HorizontalLayout().apply {
          val damage = TextField("Урон").apply {
            this.value = obj.damage.toString()
            addValueChangeListener {
              update(obj) { obj.damage = it.value.toIntOrNull()?.takeIf { it >= 1 } ?: 0 }
            }
          }
          val slash = Label("/")
          val destruction = TextField("Разрушение").apply {
            this.value = obj.desturction.toString()
            addValueChangeListener {
              update(obj) { obj.desturction = it.value.toIntOrNull()?.takeIf { it >= 1 } ?: 0 }
            }
          }
          add(damage, slash, destruction)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, damage, slash, destruction)
        },
        TextField("Дистанция").apply {
          this.value = obj.distance.toString()
          addValueChangeListener {
            update(obj) {
              obj.distance = it.value.toBigDecimalOrNull()?.takeIf { it >= BigDecimal.ZERO } ?: BigDecimal.ZERO
            }
          }
        },
        TextField("Скорость").apply {
          this.value = obj.speed.toString()
          addValueChangeListener {
            update(obj) { obj.speed = it.value.toIntOrNull() ?: 0 }
          }
        },
        HorizontalLayout().apply {
          val clipSize = TextField("Магазин").apply {
            this.value = obj.clipSize.toString()
            addValueChangeListener {
              update(obj) { obj.clipSize = it.value.toIntOrNull()?.takeIf { it >= 1 } ?: 0 }
            }
          }
          val inBarrel = Checkbox("В стволе").apply {
            this.value = obj.allowInBarrel
            addValueChangeListener {
              update(obj) { obj.allowInBarrel = it.value }
            }
          }
          add(clipSize, inBarrel)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, clipSize, inBarrel)
        }
    )
  }
}
