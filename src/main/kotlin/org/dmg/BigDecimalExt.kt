package org.dmg

import java.math.BigDecimal

fun BigDecimal.isZero() = this.compareTo(BigDecimal.ZERO) == 0