import environment.Environment
import rendering.Renderer

fun main(args: Array<String>) {
    val renderer = Renderer()

    Thread.sleep(300)

    val env = Environment(renderer.size)
    renderer.environment = env

    renderer.repaint()
}