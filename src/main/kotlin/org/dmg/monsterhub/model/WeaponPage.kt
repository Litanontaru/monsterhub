package org.dmg.monsterhub.model

import com.vaadin.flow.component.dialog.Dialog

class WeaponPage(
        val weapon: Weapon,
        val weaponService: WeaponService
): Dialog()