package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import org.dmg.monsterhub.data.FeatureData
import org.dmg.monsterhub.data.FeatureDataDesignation
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.data.meta.NumberOption.Companion.IMPORTANCE_OPTIONS
import org.dmg.monsterhub.pages.edit.data.ServiceLocator
import org.dmg.monsterhub.pages.edit.form.EditDialog
import java.math.BigDecimal

object FeatureDataSpace : Space {
  override fun support(obj: Any) = obj is FeatureData

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component> {
    val parent = mutableListOf<Component>()
    val obj = anyObj as FeatureData

    parent.add(HorizontalLayout().apply {
      val label = Label(obj.feature.name)
      val editButton = Button(Icon(VaadinIcon.EDIT)) {
        EditDialog(obj.feature, locator).open()
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }
      add(label, editButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, editButton)
    })

    parent.add(TextArea().apply {
      value = obj.feature.description
      isReadOnly = true

      width = "100%"
    })

    addNumber(parent, "X", obj.x, obj.xa, obj.xb, { update(obj) { obj.x = it } }, { update(obj) { obj.xa = it } }, { update(obj) { obj.xb = it } }, obj.feature.x)
    addNumber(parent, "Y", obj.y, obj.ya, obj.yb, { update(obj) { obj.y = it } }, { update(obj) { obj.ya = it } }, { update(obj) { obj.yb = it } }, obj.feature.y)
    addNumber(parent, "Z", obj.z, obj.za, obj.zb, { update(obj) { obj.z = it } }, { update(obj) { obj.za = it } }, { update(obj) { obj.zb = it } }, obj.feature.z)

    obj.feature.designations.forEach { key ->
      if (key.endsWith("*")) {
        val oneKey = key.substring(0, key.length - 1)
        parent.add(TextArea(oneKey).apply {
          this.value = obj.designations.find { it.designationKey == oneKey }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, oneKey, it.value, update) }

          width = "100%"
        })
      } else {
        parent.add(TextField(key).apply {
          this.value = obj.designations.find { it.designationKey == key }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, key, it.value, update) }

          width = "100%"
        })
      }
    }

    return parent
  }
}

private fun assignDesignation(obj: FeatureData, key: String, newValue: String, update: (Any, () -> Unit) -> Any) {
  obj.designations
      .find { it.designationKey == key }
      ?.also { designation -> update(designation) { designation.value = newValue } }
      ?: update(obj) {
        FeatureDataDesignation().apply {
          this.designationKey = key
          this.value = newValue

          update(this) {}
          update(obj) { obj.designations.add(this) }
        }
      }
}

private fun addNumber(
    parent: MutableList<Component>,
    label: String,
    value: BigDecimal,
    valueA: BigDecimal,
    valueB: BigDecimal,
    setter: (BigDecimal) -> Unit,
    setterA: (BigDecimal) -> Unit,
    setterB: (BigDecimal) -> Unit,
    option: NumberOption
) {
  when (option) {
    NumberOption.NONE -> {
      //do nothing
    }
    NumberOption.POSITIVE -> {
      parent.add(TextField(label).apply {
        this.value = value.stripTrailingZeros().toString()
        addValueChangeListener {
          setter(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
        }
      })
    }
    NumberOption.POSITIVE_AND_INFINITE -> parent.add(
        HorizontalLayout().apply {
          val (field, infinite) = valueWithInfinite(value.stripTrailingZeros(), label, setter)

          add(field, infinite)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, field, infinite)
        }
    )
    NumberOption.FREE -> {
      parent.add(TextField(label).apply {
        this.value = value.stripTrailingZeros().toString()
        addValueChangeListener { setter(it.value.toBigDecimalOrNull() ?: BigDecimal.ZERO) }
      })
    }
    NumberOption.DAMAGE -> parent.add(
        HorizontalLayout().apply {
          val damage = TextField(label).apply {
            this.value = value.stripTrailingZeros().toString()
            addValueChangeListener {
              setter(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
            }
          }
          val slash = Label("/")
          val destruction = TextField().apply {
            this.value = valueA.stripTrailingZeros().toString()
            addValueChangeListener {
              setterA(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
            }
          }
          add(damage, slash, destruction)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, damage, slash, destruction)
        }
    )
    NumberOption.ARMOR -> parent.add(
        HorizontalLayout().apply {
          val (field, infinite) = valueWithInfinite(value.stripTrailingZeros(), "$label Сильная", setter)
          val dash = Label("/")
          val (fieldA, infiniteA) = valueWithInfinite(valueA.stripTrailingZeros(), "Стандартная", setterA)
          val dashB = Label("/")
          val (fieldB, infiniteB) = valueWithInfinite(valueB.stripTrailingZeros(), "Слабая", setterB)

          add(field, infinite, dash, fieldA, infiniteA, dashB, fieldB, infiniteB)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, field, infinite, dash, fieldA, infiniteA, dashB, fieldB, infiniteB)
        }
    )
    NumberOption.IMPORTANCE -> {

      parent.add(ComboBox<Int>().apply {
        setItems((0..9).toList())
        setItemLabelGenerator { IMPORTANCE_OPTIONS[it] }
        this.value = value.toInt()
        addValueChangeListener { setter(it.value.toBigDecimal()) }

        width = "100%"
      })
    }
  }
}

private fun valueWithInfinite(value: BigDecimal, label: String, setter: (BigDecimal) -> Unit): Pair<TextField, Checkbox> {
  val isInfinite = value == Int.MAX_VALUE.toBigDecimal()

  val field = TextField(label).apply {
    this.value = if (isInfinite) "" else value.toString()
    isEnabled = !isInfinite

    addValueChangeListener {
      setter(it.value.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO)
    }

    width = "10em"
  }
  val inifinite = Checkbox("Бесконечность").apply {
    this.value = isInfinite

    addValueChangeListener {
      if (it.value) {
        field.value = ""
        field.isEnabled = false

        setter(Int.MAX_VALUE.toBigDecimal())
      } else {
        field.value = "0"
        field.isEnabled = true

        setter(BigDecimal.ZERO)
      }
    }
  }

  return field to inifinite
}
