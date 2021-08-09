package org.dmg.monsterhub.data.setting

import javax.persistence.*

@MappedSuperclass
open class SettingObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var name: String = ""

    @ManyToOne
    @JoinColumn(name="setting_id", nullable=true)
    lateinit var setting: Setting

    @ManyToOne
    @JoinColumn(name="parent_id", nullable=true)
    var parent: Folder? = null
}