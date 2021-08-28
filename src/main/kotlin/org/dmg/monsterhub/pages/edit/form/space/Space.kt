package org.dmg.monsterhub.pages.edit.form.space

import com.vaadin.flow.component.HasComponents
import org.dmg.monsterhub.pages.edit.data.ServiceLocator

interface Space {
  fun support(obj: Any): Boolean

  fun use(parent: HasComponents, anyObj: Any, locator: ServiceLocator, update: (Any, () -> Unit) -> Unit)
}