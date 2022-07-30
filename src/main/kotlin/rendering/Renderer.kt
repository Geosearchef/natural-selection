package rendering

import SimulationController
import environment.Being
import environment.Environment
import environment.FoodUnit
import util.math.Vector
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.roundToInt


const val FRAME_TITLE = "natural-selection"
val FOOD_COLOR = Color(250, 175, 0)

class Renderer : JPanel(), KeyListener {

    var frame = JFrame(FRAME_TITLE).apply { // wtf kotlin
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(this@Renderer)

        addKeyListener(this@Renderer)

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

        renderUI(g)
    }

    fun renderFoodUnit(g: Graphics2D, f: FoodUnit) {
        val radius = Math.sqrt(f.amount) * 3.0
        g.color = FOOD_COLOR
        g.fillCircle(f.pos, radius)
    }

    fun renderBeing(g: Graphics2D, b: Being) {
        val radius = b.health
        g.color = b.agent?.color ?: Color(0, 133, 115)
        g.fillCircle(b.pos, radius)

        // eye
        val eyePos = b.pos + b.direction.normalise() * radius.toDouble() * 0.7
        g.color = Color(191, 191, 191)
        g.fillCircle(eyePos, radius / 3)
    }

    fun renderUI(g: Graphics2D) {
        val env = environment ?: return

        val oldFont = g.font

        g.font = g.font.deriveFont(17.0f)

        g.color = Color.BLACK
        g.drawString("generation: %d".format(env.generation), 8, 20)
        g.drawString("alive: %d".format(env.beings.size), 8, 40)
        g.drawString("time: %.2f s".format(env.simulationTime), 8, 80)
        g.drawString("delta: %d ms".format((env.averageDelta * 1000.0).roundToInt()), 8, 100)


        g.drawString("avg. neurons: %.1f".format(env.stats.avgNeuronsPerAgent), 8, 160)
        g.drawString("avg. synapses: %.1f".format(env.stats.avgSynapsesPerAgent), 8, 180)



        g.font = oldFont
    }

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent?) {
        if(e == null) {
            return
        }

        if(e.keyCode == 'W'.code) {
            SimulationController.toggleWarp()
        }

    }

    override fun keyReleased(e: KeyEvent?) {}
}