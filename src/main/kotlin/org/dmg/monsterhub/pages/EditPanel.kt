package org.dmg.monsterhub.pages

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import org.dmg.monsterhub.data.*
import org.dmg.monsterhub.data.meta.Feature
import org.dmg.monsterhub.data.meta.FeatureContainer
import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.dmg.monsterhub.data.meta.NumberOption
import org.dmg.monsterhub.data.setting.SettingObject
import org.dmg.monsterhub.repository.FeatureContainerItemRepository
import org.dmg.monsterhub.repository.FeatureDataDesignationRepository
import org.dmg.monsterhub.service.FeatureContainerServiceLocator
import org.dmg.monsterhub.service.FeatureDataRepository

class EditPanel(
    private val obj: Any,
    private val data: ObjectTreeDataProvider,
    private val fiderData: ObjectFinderDataProviderForSetting,
    private val featureDataRepository: FeatureDataRepository,
    private val featureContainerItemRepository: FeatureContainerItemRepository,
    private val featureDataDesignationRepository: FeatureDataDesignationRepository,
    private val featureContainerServiceLocator: FeatureContainerServiceLocator,
    private val onUpdate: (() -> Unit)? = null
) : VerticalLayout() {
  init {
    if (obj is SettingObject) {
      settingObjectSpace(obj)
    }
    if (obj is Feature) {
      featureSpace(obj)
    }

    if (obj is FeatureContainer) {
      featureContainerSpace(obj)
    }

    if (obj is Trait) {
      traitSpace(obj)
    }

    if (obj is Creature) {
      creatureSpace(obj)
    }

    if (obj is FeatureData) {
      featureDataSpace(obj)
    }

    if (obj is FeatureContainerData) {
      featureContainerDataSpace(obj)
    }

    height = "100%"
    width = "100%"
    isPadding = false
    isSpacing = false
  }

  private fun settingObjectSpace(obj: SettingObject) {
    add(TextField("Название").apply {
      value = obj.name
      addValueChangeListener {
        update { obj.name = it.value }
      }
      width = "100%"
    })
  }

  private fun featureSpace(obj: Feature) {
    add(TextArea("Описание").apply {
      value = obj.description
      addValueChangeListener {
        update { obj.description = it.value }
      }
      width = "100%"
    })

    add(HorizontalLayout().apply {
      add(ComboBox<String>("X").apply {
        setItems(NumberOption.display)
        value = obj.x.displayName
        addValueChangeListener {
          update { obj.x = NumberOption(it.value) }
        }
        width = "100%"
      })

      add(ComboBox<String>("Y").apply {
        setItems(NumberOption.display)
        value = obj.y.displayName
        addValueChangeListener {
          update { obj.y = NumberOption(it.value) }
        }
        width = "100%"
      })

      add(ComboBox<String>("Z").apply {
        setItems(NumberOption.display)
        value = obj.z.displayName
        addValueChangeListener {
          update { obj.z = NumberOption(it.value) }
        }
        width = "100%"
      })

      add(TextField("Группа").apply {
        value = obj.selectionGroup ?: ""
        addValueChangeListener {
          update { obj.selectionGroup = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      })

      add(TextField("Категория").apply {
        value = obj.category
        addValueChangeListener {
          update { obj.category = it.value }
        }
        width = "100%"
      })

      width = "100%"
      isPadding = false
    })

    add(TextArea("Указывать").apply {
      value = obj.designations.joinToString("\n")
      addValueChangeListener {
        update { obj.designations = it.value.lines().filter { it.isNotBlank() } }
      }
      width = "100%"
    })
  }

  private fun featureContainerSpace(obj: FeatureContainer) {
    val dataProvider = FeatureContainerItemDataProvider(
        obj,
        { update(it) {} }
    )

    add(HorizontalLayout().apply {
      val label = Label("Дополнительные свойства")

      val addNew = TextField()

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        addNew.optionalValue.ifPresent {
          val newFeatureContainerItem = FeatureContainerItem().apply { featureType = addNew.value }
          featureContainerItemRepository.save(newFeatureContainerItem)
          dataProvider.add(newFeatureContainerItem)
        }
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }

      add(label, addNew, addButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
    })

    val grid = Grid<FeatureContainerItem>().apply {
      fun edit(containerItem: FeatureContainerItem) {
        FeatureContaiterItemEditDialog(containerItem) {
          featureContainerItemRepository.save(it)
          dataProvider.refreshItem(it)
        }.open()
      }

      addItemDoubleClickListener { edit(it.item) }

      addColumn { it.featureType }
      addColumn { it.name }
      addColumn { it.onlyOne }

      addComponentColumn { containerItem ->
        HorizontalLayout().apply {
          add(Button(Icon(VaadinIcon.EDIT)) {
            edit(containerItem)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          })

          add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
            dataProvider.delete(containerItem)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          })

          isPadding = false
        }

      }
      setItems(dataProvider as DataProvider<FeatureContainerItem, Void>)

      width = "100%"
      isHeightByRows = true
    }

    add(grid)
  }

  private fun traitSpace(obj: Trait) {
    add(Label("Показатели черты"))

    add(HorizontalLayout().apply {
      val offence = VerticalLayout().apply {
        add(TextField("Напападение").apply {
          value = obj.offenceBase ?: ""
          addValueChangeListener {
            update { obj.offenceBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.offenceAlt ?: ""
          addValueChangeListener {
            update { obj.offenceAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })
        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val defence = VerticalLayout().apply {
        add(TextField("Защита").apply {
          value = obj.defenceBase ?: ""
          addValueChangeListener {
            update { obj.defenceBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.defenceAlt ?: ""
          addValueChangeListener {
            update { obj.defenceAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val perception = VerticalLayout().apply {
        add(TextField("Восприятие").apply {
          value = obj.perceptionBase ?: ""
          addValueChangeListener {
            update { obj.perceptionBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.perceptionAlt ?: ""
          addValueChangeListener {
            update { obj.perceptionAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val hands = VerticalLayout().apply {
        add(TextField("Манипуляторы").apply {
          value = obj.handsBase ?: ""
          addValueChangeListener {
            update { obj.handsBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.handsAlt ?: ""
          addValueChangeListener {
            update { obj.handsAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val move = VerticalLayout().apply {
        add(TextField("Движение").apply {
          value = obj.moveBase ?: ""
          addValueChangeListener {
            update { obj.moveBase = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        add(TextField().apply {
          value = obj.moveAlt ?: ""
          addValueChangeListener {
            update { obj.moveAlt = it.value.takeIf { it.isNotBlank() } }
          }
          width = "100%"
        })

        width = "100%"
        isPadding = false
        isSpacing = false
      }
      val common = TextField("Общее").apply {
        value = obj.common ?: ""
        addValueChangeListener {
          update { obj.common = it.value.takeIf { it.isNotBlank() } }
        }
        width = "100%"
      }

      add(offence, defence, perception, hands, move, common)
      expand(offence, defence, perception, hands, move, common)

      width = "100%"
      isPadding = false
    })
  }

  private fun creatureSpace(obj: Creature) {
    val dataProvider = CreatureHierarchyDataProvider(
        obj,
        { update(it) {} }
    )

    add(HorizontalLayout().apply {
      val label = Label("Основа")

      val addNew = ComboBox<SettingObject>().apply {
        setItems(fiderData("CREATURE") as DataProvider<SettingObject, String>)
        setItemLabelGenerator { it.name }
      }

      val addButton = Button(Icon(VaadinIcon.PLUS)) {
        addNew.optionalValue.ifPresent {
          dataProvider.add(it as Creature)
          addNew.value = null
        }
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }

      add(label, addNew, addButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
    })

    val grid = Grid<Creature>().apply {
      addColumn { it.name }
      addComponentColumn { base ->
        HorizontalLayout().apply {
          add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
            dataProvider.delete(base)
          }.apply {
            addThemeVariants(ButtonVariant.LUMO_SMALL)
          })

          isPadding = false
        }
      }
      setItems(dataProvider as DataProvider<Creature, Void>)

      width = "100%"
      isHeightByRows = true
    }

    add(grid)
  }

  private fun featureContainerDataSpace(obj: FeatureContainerData) {
    val meta = featureContainerServiceLocator.containerMeta(obj)
    if (meta != null) {
      meta.containFeatureTypes.forEach { type ->
        if (type.onlyOne) {
          val place = VerticalLayout().apply {
            width = "100%"
            isPadding = false
            isSpacing = false
          }
          add(place)

          var featurePanel: Component? = null
          fun updateOneFeaturePanel() {
            if (featurePanel != null) {
              place.remove(featurePanel)
            }

            val existing = obj.features.find { it.feature.featureType == type.featureType }
            featurePanel = if (existing == null) {
              HorizontalLayout().apply {
                val label = Label(type.name)

                val addNew = ComboBox<SettingObject>().apply {
                  setItems(fiderData(type.featureType) as DataProvider<SettingObject, String>)
                  setItemLabelGenerator { it.name }
                }

                val addButton = Button(Icon(VaadinIcon.PLUS)) {
                  addNew.optionalValue.ifPresent {
                    val newFeatureData = FeatureData().apply { feature = it as Feature }
                    featureDataRepository.save(newFeatureData)
                    obj.features.add(newFeatureData)
                    update {}

                    updateOneFeaturePanel()
                  }
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }

                add(label, addNew, addButton)
                setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
              }
            } else {
              HorizontalLayout().apply {
                val label = Label(existing.display())

                val editButton = Button(Icon(VaadinIcon.EDIT)) {
                  EditDialog(existing, data, fiderData, featureDataRepository, featureContainerItemRepository, featureDataDesignationRepository, featureContainerServiceLocator) {
                    featureDataRepository.save(existing)
                    label.text = existing.display()
                  }.open()
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }
                val closeButton = Button(Icon(VaadinIcon.CLOSE_SMALL)) {
                  update { obj.features.remove(existing) }
                  featureDataRepository.delete(existing)

                  updateOneFeaturePanel()
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                }

                add(label, editButton, closeButton)
                setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, editButton, closeButton)
              }
            }
            place.add(featurePanel)
          }

          updateOneFeaturePanel()
        } else {
          val dataProvider = FeatureDataDataProvider(
              type.featureType,
              obj,
              { update(it) {} }
          )

          add(HorizontalLayout().apply {
            val label = Label(type.name)

            val addNew = ComboBox<SettingObject>().apply {
              setItems(fiderData(type.featureType) as DataProvider<SettingObject, String>)
              setItemLabelGenerator { it.name }
            }

            val addButton = Button(Icon(VaadinIcon.PLUS)) {
              addNew.optionalValue.ifPresent {
                val newFeatureData = FeatureData().apply { feature = it as Feature }
                featureDataRepository.save(newFeatureData)
                dataProvider.add(newFeatureData)
                addNew.value = null
              }
            }.apply {
              addThemeVariants(ButtonVariant.LUMO_SMALL)
            }

            add(label, addNew, addButton)
            setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, addNew, addButton)
          })

          val grid = Grid<FeatureData>().apply {
            fun edit(item: FeatureData) {
              EditDialog(item, data, fiderData, featureDataRepository, featureContainerItemRepository, featureDataDesignationRepository, featureContainerServiceLocator) {
                dataProvider.update(item)
              }.open()
            }

            addItemDoubleClickListener { edit(it.item) }

            addColumn { it.display() }

            addComponentColumn { featureData ->
              HorizontalLayout().apply {
                add(Button(Icon(VaadinIcon.EDIT)) {
                  edit(featureData)
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                })

                add(Button(Icon(VaadinIcon.CLOSE_SMALL)) {
                  dataProvider.delete(featureData)
                }.apply {
                  addThemeVariants(ButtonVariant.LUMO_SMALL)
                })

                isPadding = false
              }

            }
            setItems(dataProvider as DataProvider<FeatureData, Void>)

            width = "100%"
            isHeightByRows = true
          }

          add(grid)
        }
      }
    }
  }

  private fun featureDataSpace(obj: FeatureData) {
    add(HorizontalLayout().apply {
      val label = Label(obj.feature.name)
      val editButton = Button(Icon(VaadinIcon.EDIT)) {
        EditDialog(obj.feature, data, fiderData, featureDataRepository, featureContainerItemRepository, featureDataDesignationRepository, featureContainerServiceLocator).open()
      }.apply {
        addThemeVariants(ButtonVariant.LUMO_SMALL)
      }
      add(label, editButton)
      setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, label, editButton)
    })

    add(TextArea().apply {
      value = obj.feature.description
      isReadOnly = true

      width = "100%"
    })

    addNumber("X", obj.x, obj.xa, { update { obj.x = it } }, { update { obj.xa = it } }, obj.feature.x)
    addNumber("Y", obj.y, obj.ya, { update { obj.y = it } }, { update { obj.ya = it } }, obj.feature.y)
    addNumber("Z", obj.z, obj.za, { update { obj.z = it } }, { update { obj.za = it } }, obj.feature.z)

    obj.feature.designations.forEach { key ->
      if (key.endsWith("*")) {
        val oneKey = key.substring(0, key.length - 1)
        add(TextArea(oneKey).apply {
          this.value = obj.designations.find { it.designationKey == oneKey }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, oneKey, it.value) }

          width = "100%"
        })
      } else {
        add(TextField(key).apply {
          this.value = obj.designations.find { it.designationKey == key }?.value ?: ""
          addValueChangeListener { assignDesignation(obj, key, it.value) }

          width = "100%"
        })
      }
    }
  }

  private fun assignDesignation(obj: FeatureData, key: String, newValue: String) {
    obj.designations
        .find { it.designationKey == key }
        ?.run { update { this.value = newValue } }
        ?: update {
          val featureDataDesignation = featureDataDesignationRepository.save(
              FeatureDataDesignation().apply {
                this.designationKey = key
                this.value = newValue
              }
          )
          obj.designations.add(featureDataDesignation)
        }
  }

  private fun addNumber(
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
      NumberOption.POSITIVE_AND_INFINITE -> TODO()
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

  private fun update(action: () -> Unit) {
    update(obj, action)
  }

  private fun update(obj: Any, action: () -> Unit) {
    action()
    when (obj) {
      is SettingObject -> data.update(obj)
      is FeatureData -> featureDataRepository.save(obj)
    }
    if (onUpdate != null && obj == this.obj) {
      onUpdate!!()
    }
  }
}