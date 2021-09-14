package org.dmg.monsterhub.pages.edit.form

data class EditPanelConfig(
    var selectedTab: String,
    val spaces: MutableMap<Any, Any> = mutableMapOf()
)