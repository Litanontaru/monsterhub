package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.DBObject
import org.dmg.monsterhub.service.Write
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface DBObjectRepository<T : DBObject> : JpaRepository<T, Long>

fun <T : DBObject> DBObjectRepository<T>.update(dbObject: DBObject, callback: (T) -> Unit = {}) {
  Write behind {
    when {
      dbObject.deleteOnly -> delete(dbObject as T)
      else -> callback(save(dbObject as T))
    }

  }
}