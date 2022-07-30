package util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min

object ColorUtil {
    const val DEFAULT_AGENT_SATURATION = 0.8f
    const val DEFAULT_AGENT_BRIGHTNESS = 0.8f

    fun randomColor() = Color.getHSBColor(Math.random().toFloat(), DEFAULT_AGENT_SATURATION, DEFAULT_AGENT_BRIGHTNESS)

    fun randomShiftColor(c: Color): Color {
        val hsbValues = FloatArray(3)
        Color.RGBtoHSB(c.red, c.green, c.blue, hsbValues)
        return Color.getHSBColor(
            hsbValues[0] + (Math.random().toFloat() - 0.5f) * 0.08f,
            hsbValues[1],
            max(0.3f, min(0.9f, hsbValues[2] + (Math.random().toFloat() - 0.5f) * 0.08f))
        )
    }
}
