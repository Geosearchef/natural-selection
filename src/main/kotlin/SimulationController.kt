import agent.Agent
import agent.network.Network
import agent.network.NetworkEvolver
import environment.Environment
import rendering.Renderer
import java.util.concurrent.CompletableFuture
import javax.swing.SwingUtilities

object SimulationController {
    const val WARP_SPEED = 0.04 // update step size
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

                    Thread.sleep(16)
                }

                println(env.simulationTime)

                if(env.simulationTime > SIMULATION_ROUND_TIME_LIMIT || env.beings.size.toDouble() < 0.5 * INITIAL_AGENTS.toDouble()) {
                    break
                }
            }

            // remove agents of dead beings
            agents.removeIf { agent -> env.beings.none { it.agent == agent } }

            println("Agents left: ${agents.size}, reproducing...")

            // Select, reproduce and evolve
            while(agents.size < INITIAL_AGENTS) {
                val newAgent = Agent(agents.random().net.clone()) // TODO: THIS INCLUDES THE NEWLY CREATED AGENTS
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