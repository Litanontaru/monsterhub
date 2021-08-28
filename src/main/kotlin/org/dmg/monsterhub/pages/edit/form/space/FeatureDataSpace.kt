package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
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
import org.dmg.monsterhub.pages.edit.form.EditDialog
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

object FeatureDataSpace: Space {
  override fun support(obj: Any) = obj is FeatureData

  override fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
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

    parent.addNumber("X", obj.x, obj.xa, { update(obj) { obj.x = it } }, { update(obj) { obj.xa = it } }, obj.feature.x)
    parent.addNumber("Y", obj.y, obj.ya, { update(obj) { obj.y = it } }, { update(obj) { obj.ya = it } }, obj.feature.y)
    parent.addNumber("Z", obj.z, obj.za, { update(obj) { obj.z = it } }, { update(obj) { obj.za = it } }, obj.feature.z)

    obj.feature.designations.forEach { key ->
      if (key.endsWith("*")) {
        val oneKey = key.substring(0, key.length - 1)
        parent.add(TextArea(oneKey).apply {
          this.value = obj.designations.find { it.designationKey == oneKey }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, oneKey, it.value, locator, update) }

          width = "100%"
        })
      } else {
        parent.add(TextField(key).apply {
          this.value = obj.designations.find { it.designationKey == key }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, key, it.value, locator, update) }

          width = "100%"
        })
      }
    }
  }
}

private fun assignDesignation(obj: FeatureData, key: String, newValue: String, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) {
  obj.designations
      .find { it.designationKey == key }
      ?.run { update(obj) { this.value = newValue } }
      ?: update(obj) {
        val featureDataDesignation = locator.featureDataDesignationRepository.save(
            FeatureDataDesignation().apply {
              this.designationKey = key
              this.value = newValue
            }
        )
        obj.designations.add(featureDataDesignation)
      }
}

private fun HasComponents.addNumber(
    label: String,
    value: Int,
    valueA: Int,
    setter: (Int) -> Unit,
    setterA: (Int) -> Unit,
    option: NumberOption
) {
  when (option) {
    NumberOption.NONE -> {
      //do nothing
    }
    NumberOption.POSITIVE -> {
      add(TextField(label).apply {
        this.value = value.toString()
        addValueChangeListener { setter(it.value.toIntOrNull()?.takeIf { it >= 0 } ?: 0) }
      })
    }
    NumberOption.POSITIVE_AND_INFINITE -> add(
        HorizontalLayout().apply {
          val isInfinite = value == Int.MAX_VALUE

          val field = TextField(label).apply {
            this.value = if (isInfinite) "" else value.toString()
            isEnabled = !isInfinite

            addValueChangeListener {
              setter(it.value.toIntOrNull()?.takeIf { it >= 0 } ?: 0)
            }
          }
          val inifinite = Checkbox("Бесконечность").apply {
            this.value = isInfinite

            addValueChangeListener {
              if (it.value) {
                field.value = ""
                field.isEnabled = false

                setter(Int.MAX_VALUE)
              } else {
                field.value = "0"
                field.isEnabled = true

                setter(0)
              }
            }
          }

          add(field, inifinite)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, field, inifinite)
        }
    )
    NumberOption.FREE -> {
      add(TextField(label).apply {
        this.value = value.toString()
        addValueChangeListener { setter(it.value.toIntOrNull() ?: 0) }
      })
    }
    NumberOption.DAMAGE -> add(
        HorizontalLayout().apply {
          val damage = TextField(label).apply {
            this.value = value.toString()
            addValueChangeListener { setter(it.value.toIntOrNull()?.takeIf { it >= 1 } ?: 0) }
          }
          val slash = Label("/")
          val destruction = TextField().apply {
            this.value = valueA.toString()
            addValueChangeListener { setterA(it.value.toIntOrNull()?.takeIf { it >= 1 } ?: 0) }
          }
          add(damage, slash, destruction)
          setVerticalComponentAlignment(FlexComponent.Alignment.END, damage, slash, destruction)
        }
    )
    NumberOption.IMPORTANCE -> {
      val options = listOf(
          "Никогда или Никакую роль",
          "Малую Редко",
          "Важную Редко",
          "Малую Вероятно",
          "Эпическую Редко",
          "Малую Часто",
          "Важную Вероятно",
          "Важную Часто",
          "Эпическую Вероятно",
          "Эпическую Часто"
      )

      add(ComboBox<Int>().apply {
        setItems((0..9).toList())
        setItemLabelGenerator { options[it] }
        this.value = value
        addValueChangeListener { setter(it.value.takeIf { it >= 0 && it <= 9 } ?: 0) }

        width = "100%"
      })
    }
  }
}
