package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.DBObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface DBObjectRepository<T : DBObject> : JpaRepository<T, Long> {
  fun update(dbObject: DBObject): T = when {
    dbObject.deleteOnly -> {
      delete(dbObject as T)
      dbObject
    }
    else -> save(dbObject as T)
  }
}