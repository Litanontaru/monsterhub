package org.dmg.monsterhub.service

import java.util.concurrent.LinkedBlockingDeque

object Write {
  private val queue = LinkedBlockingDeque<() -> Unit>()

  init {
    Thread {
      while (true) {
        val poll = queue.poll()
        if (poll != null) {
          poll.invoke()
        }
      }
    }.apply {
      name = "write-behind-service"
    }.start()
  }

  infix fun behind(task: () -> Unit) {
    queue.add(task)
  }
}