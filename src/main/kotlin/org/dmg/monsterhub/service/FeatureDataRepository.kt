package org.dmg.monsterhub.service

import org.dmg.monsterhub.data.FeatureData
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureDataRepository: JpaRepository<FeatureData, Long>