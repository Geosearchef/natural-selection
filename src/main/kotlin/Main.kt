import environment.Environment
import rendering.Renderer
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    val renderer = Renderer()

    Thread.sleep(300)

    val env = Environment(renderer.size)
    renderer.environment = env

    var time = System.currentTimeMillis()
    while(true) {
        val deltaMs = System.currentTimeMillis() - time
        time += deltaMs
        val delta = deltaMs.toDouble() / 1000.0
        println(delta)

        env.update(delta)
        SwingUtilities.invokeLater {
            renderer.repaint()
        }

        Thread.sleep(16)
    }
}