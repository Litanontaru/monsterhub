package org.dmg.monsterhub.repository

import org.dmg.monsterhub.data.DBObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.concurrent.CompletableFuture

@NoRepositoryBean
interface DBObjectRepository<T : DBObject> : JpaRepository<T, Long>

fun <T : DBObject> DBObjectRepository<T>.updateAsunc(dbObject: DBObject): CompletableFuture<T> {
  return CompletableFuture.supplyAsync {
    update(dbObject)
  }
}

fun <T : DBObject> DBObjectRepository<T>.update(dbObject: DBObject): T {
  return when {
    dbObject.deleteOnly -> {
      delete(dbObject as T)
      dbObject
    }
    else -> save(dbObject as T)
  }
}