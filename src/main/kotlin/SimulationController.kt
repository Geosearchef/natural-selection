import agent.Agent
import agent.network.Network
import agent.network.NetworkEvolver
import environment.Environment
import rendering.Renderer
import util.ColorUtil
import java.util.concurrent.CompletableFuture
import javax.swing.SwingUtilities
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object SimulationController {
    const val BEING_MAX_SPEED = 30.0
    const val BEING_TURN_FACTOR = 12.0
    const val WARP_SPEED = 0.20 // update step size
    const val SIMULATION_ROUND_TIME_LIMIT = 30.0
    const val INITIAL_AGENTS = 50

    val renderer = Renderer()

    var env = Environment(renderer.size, 0)
    val agents = ArrayList<Agent>()

    val networkEvolver = NetworkEvolver()

    var warp = false

    init {
        Thread.sleep(300)

        repeat(INITIAL_AGENTS) {
            val net = Network()
            networkEvolver.evolve(net)
            Agent.initNetworkBoundaries(net)
            val agent = Agent(net)
            agents.add(agent)
        }
    }

    fun initialSetup() {
        env = Environment(renderer.size, env.generation + 1)
        renderer.environment = env

        env.generateBeings(agents)
        env.generateFood(100)
    }


    fun start() {
        while(true) {
            initialSetup()

            var time = System.currentTimeMillis()
            while(true) {
                val deltaMs = System.currentTimeMillis() - time
                time += deltaMs
                var delta = deltaMs.toDouble() / 1000.0

                if(warp) {
                    delta = WARP_SPEED
                }

                env.update(delta)

                if(!warp || env.simulationTime == delta || env.simulationTime.mod(12.0) < 0.05) {
                    val renderFuture: CompletableFuture<Any> = CompletableFuture()
                    SwingUtilities.invokeLater {
                        renderer.repaint()
                        renderFuture.complete(null)
                    }
                    renderFuture.join()

                    if(!warp && delta < 0.016) {
                        Thread.sleep(16 - (delta * 1000).roundToLong())
                    }
                }

                if(env.simulationTime > SIMULATION_ROUND_TIME_LIMIT || env.beings.size.toDouble() < 0.5 * INITIAL_AGENTS.toDouble()) {
                    break
                }
            }

            // remove agents of dead beings
            agents.removeIf { agent -> env.beings.none { it.agent == agent } }

            println("Agents left: ${agents.size}, reproducing...")

            val averageBeingHealth = env.beings.map { it.health }.average()
            val agentsToReproduce = ArrayList(agents.filter { it.ownBeing!!.health > 0.5 * averageBeingHealth })
            agentsToReproduce.addAll(agentsToReproduce.filter { it.ownBeing!!.health > 1.25 * averageBeingHealth })

            repeat((agentsToReproduce.size.toDouble() * 0.1).roundToInt()) {
                val net = Network() // TODO: duplicate code
                networkEvolver.evolve(net)
                Agent.initNetworkBoundaries(net)
                agentsToReproduce.add(Agent(net))
            }

            // Select, reproduce and evolve
            while(agents.size < INITIAL_AGENTS) {
                val randomAgent = agentsToReproduce.random()
                val newAgent = Agent(randomAgent.net.clone(), ColorUtil.randomShiftColor(randomAgent.color))
                networkEvolver.evolve(newAgent.net)

                agents.add(newAgent)

                // TODO: could use a more intelligent reproduction schedule here
            }

            println("Agents for next round: ${agents.size}")

//            // Evolve
//            agents.forEach {
//                networkEvolver.evolve(it.net)
//            }
        }
    }

    fun toggleWarp() {
        warp = !warp
    }
}

fun main(args: Array<String>) {
    SimulationController.start()
}