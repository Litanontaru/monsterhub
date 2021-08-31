package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import org.dmg.monsterhub.data.Creature
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

interface AbstractCreatureSpace : Space {
  override fun support(obj: Any) = obj is Creature

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit) =
      use(anyObj as Creature, locator, update)

  fun use(obj: Creature, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit): List<Component>
}