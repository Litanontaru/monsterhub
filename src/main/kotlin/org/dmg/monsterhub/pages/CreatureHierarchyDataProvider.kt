package org.dmg.monsterhub.pages

import com.vaadin.flow.data.provider.AbstractDataProvider
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.Query
import org.dmg.monsterhub.data.Creature
import java.util.stream.Stream

class CreatureHierarchyDataProvider(
    val hierarchical: Creature,
    val save: (Creature) -> Unit
) : AbstractDataProvider<Creature, Unit>(), DataProvider<Creature, Unit> {
  override fun isInMemory(): Boolean = true

  override fun fetch(query: Query<Creature, Unit>?): Stream<Creature> =
      hierarchical.base.distinct().stream()
          .skip(query?.offset?.toLong() ?: 0)
          .limit(query?.limit?.toLong() ?: 0)

  override fun size(query: Query<Creature, Unit>?): Int = hierarchical.base.distinct().size

  fun add(obj: Creature) {
    hierarchical.base.add(obj)
    hierarchical.base = hierarchical.base.distinct().toMutableList()
    save(hierarchical)
    refreshAll()
  }

  fun delete(obj: Creature) {
    hierarchical.base = hierarchical.base.distinct().toMutableList()
    hierarchical.base.remove(obj)
    save(hierarchical)
    refreshAll()
  }
}