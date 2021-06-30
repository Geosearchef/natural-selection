package rendering

import environment.Being
import environment.Environment
import environment.FoodUnit
import util.math.Vector
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JFrame
import javax.swing.JPanel


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

    override fun paintComponent(graphics: Graphics?) {
        val g = graphics?.create() as? Graphics2D ?: return
        g.setRenderingHints(RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
        ))

        g.clearRect(0, 0, width, height)

        val env = environment ?: return

        env.entities.forEach { entity ->
            when(entity) {
                is FoodUnit -> renderFoodUnit(g, entity)
                is Being -> renderBeing(g, entity)
            }
        }
    }

    fun renderFoodUnit(g: Graphics2D, f: FoodUnit) {
        val radius = Math.sqrt(f.amount) * 3.0
        g.color = FOOD_COLOR
        g.fillCircle(f.pos, radius)
    }

    fun renderBeing(g: Graphics2D, b: Being) {
        val radius = b.health
        g.color = Color(0, 133, 115)
        g.fillCircle(b.pos, radius)

        // eye
        val eyePos = b.pos + b.direction.normalise() * radius.toDouble() * 0.7
        g.color = Color(191, 191, 191)
        g.fillCircle(eyePos, radius / 3)
    }

}