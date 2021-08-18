package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.meta.FeatureContainerItem
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureContainerItemRepository : JpaRepository<FeatureContainerItem, Long>