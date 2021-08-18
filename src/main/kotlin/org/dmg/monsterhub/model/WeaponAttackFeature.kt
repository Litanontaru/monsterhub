package org.dmg.monsterhub.model

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "old_weapon_attack_feature")
class WeaponAttackFeature : AbstractWeaponFeature()