package rendering

import util.math.Vector
import java.awt.Graphics2D
import kotlin.math.roundToInt

fun Graphics2D.fillCircle(center: Vector, radius: Double) {
    fillOval(
        (center.x - radius).roundToInt(),
        (center.y - radius).roundToInt(),
        (radius * 2.0).roundToInt(),
        (radius * 2.0).roundToInt()
    )
}