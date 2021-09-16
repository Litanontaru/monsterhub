package org.dmg.monsterhub.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService {
  @Transactional
  operator fun <T : Any> invoke(task: () -> T) = try {
    task()
  } catch (e: Exception) {
    throw RuntimeException(e)
  }
}