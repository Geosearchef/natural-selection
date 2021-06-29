package rendering

import environment.Being
import environment.Environment
import environment.FoodUnit
import util.math.Vector
import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.roundToInt

const val FRAME_TITLE = "natural-selection"
val FOOD_COLOR = Color(250, 175, 0)

class Renderer : JPanel() {

    var frame = JFrame(FRAME_TITLE).apply { // wtf kotlin
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(this@Renderer)
        setSize(1600, 900)
        isVisible = true
    }

    val size get() = Vector(width.toDouble(), height.toDouble())

    var environment: Environment? = null

    override fun paintComponent(g: Graphics?) {
        if(g == null) {
            return
        }

        g.clearRect(0, 0, width, height)

        val env = environment ?: return

        env.entities.forEach { entity ->
            when(entity) {
                is FoodUnit -> renderFoodUnit(g, entity)
                is Being -> renderBeing(g, entity)
            }
        }
    }

    fun renderFoodUnit(g: Graphics, f: FoodUnit) {
        val radius = f.amount * 4
        g.color = FOOD_COLOR
        g.fillOval(
            (f.pos.x - radius / 2.0).roundToInt(),
            ((f.pos.y - radius / 2.0).roundToInt()),
            (radius * 2.0).roundToInt(),
            (radius * 2.0).roundToInt()
        )
    }

    fun renderBeing(g: Graphics, b: Being) {

    }

}