package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.Component
import org.dmg.monsterhub.data.GameCharacter
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

interface AbstractGameCharacterSpace : Space {
  override fun support(obj: Any) = obj is GameCharacter

  override fun use(anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Any) =
      use(anyObj as GameCharacter, locator, update)

  fun use(obj: GameCharacter, locator: ServiceLocator, update: (Any, () -> Unit) -> Any): List<Component>
}