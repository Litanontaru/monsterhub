package org.dmg.monsterhub.data.setting

import javax.persistence.*

@Entity
class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    var name: String = ""

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
            name = "base_setting",
            joinColumns = [JoinColumn(name = "setting_id")],
            inverseJoinColumns = [JoinColumn(name = "base_id")]
    )
    var base: MutableList<Setting> = mutableListOf()

    var description: String = ""
}