package org.dmg.monsterhub.model.traits

data class ValueRate(val initValue: String) {
    private val value: Int
    private val firstArgument: Double
    private val firstUseSecondVariable: Boolean
    private val squareFirst: Boolean
    private val secondArgument: Double

    init {
        val stickMinus = initValue.replace("- ", "-")
        val parts = stickMinus
                .split("\\s+".toRegex())
                .asSequence()
                .filter { it != "+" }
                .toList()
        value = parts
                .map {
                    try {
                        Integer.parseInt(it)
                    } catch (e: Exception) {
                        0
                    }
                }
                .sum()
        val x = parts.indexOfFirst { it.contains("X") }.takeIf { it >= 0 }

        val restX = x?.let { parts[it] }

        firstArgument = restX?.parseArgument() ?: 0.0
        squareFirst = restX?.contains("XX") ?: false
        firstUseSecondVariable = restX?.contains("Y") ?: false

        secondArgument = parts
                .drop(x ?: 0)
                .drop(1)
                .find { it.contains("Y") }
                ?. parseArgument()
                ?: 0.0
    }

    private fun String.parseArgument(): Double? {
        val r = this.replace("X", "").replace("Y", "")

        return when {
            r.isEmpty() -> 1.0
            r == "-" -> -1.0
            else -> try {
                if (r.contains("/")) 1.0 / java.lang.Double.parseDouble(r.replace("/", "")) else java.lang.Double.parseDouble(r)
            } catch (e: NumberFormatException) {
                throw RuntimeException("Cannot parse " + this, e)
            }
        }

    }

    fun evaluate(x: Int, y: Int, speed: Double): Int {
        val v = value.toDouble() + firstArgument * ((if (squareFirst) x * x else x) * if (firstUseSecondVariable) y else 1) + secondArgument * y
        return (v * speed).toInt()
    }
}