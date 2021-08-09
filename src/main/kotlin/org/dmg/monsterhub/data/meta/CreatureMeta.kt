package org.dmg.monsterhub.data.meta

object CreatureMeta: FeatureContainer {
    override val containFeatureTypes: List<FeatureContainerItem> = listOf(
            FeatureContainerItem().apply {
                name = "Черты"
                featureType = "TRAIT"

            }
    )
}