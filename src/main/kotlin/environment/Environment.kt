package environment

import agent.Agent
import util.math.Vector

class Environment(val size: Vector, val generation: Int) {

    data class EnvironmentStats(val avgNeuronsPerAgent: Double, val avgSynapsesPerAgent: Double)
    private var privateStats: EnvironmentStats? = null
    val stats: EnvironmentStats get() {
        if(privateStats == null) {
            privateStats = EnvironmentStats(
                beings.map { it.agent?.net }.sumOf { it?.neurons?.size ?: 0 }.toDouble() / beings.size,
                beings.map { it.agent?.net }.sumOf { it?.synapses?.size ?: 0 }.toDouble() / beings.size
            )
        }
        return privateStats!!
    }

    var averageDelta = 0.100
    var simulationTime: Double = 0.0

    val entities = ArrayList<Entity>()
    val beings get() = entities.filterIsInstance<Being>()
    val foodUnits get() = entities.filterIsInstance<FoodUnit>()

    fun update(delta: Double) {
        simulationTime += delta
        averageDelta = averageDelta * 0.9 + delta * 0.1

        val entitiesCopy = ArrayList(entities)
        entitiesCopy.forEach {
            val alive = it.update(delta, this)
            if(!alive) {
                this.entities.remove(it)
            }
        }

        entities.filterIsInstance<Being>().forEach { it.agent?.update(delta) }
    }

    fun generateBeings(agents: List<Agent?>) {
        agents.forEach { agent ->
            val pos = randomPosition()

            if(beings.none { (it.pos - pos).lengthSquared() < 20*20 }) {
                val being = Being(pos, agent)
                entities.add(being)

                agent?.ownBeing = being
                agent?.env = this
            }
        }
    }

    fun generateFood(count: Int) {
        repeat(count) {
            val pos = randomPosition()

            if(foodUnits.none { (it.pos - pos).lengthSquared() < 10*10 }) {
                entities.add(FoodUnit(pos, 3.0))
            }
        }
    }

    fun randomPosition() = Vector(
        (10 until size.x.toInt() - 10).random().toDouble(),
        (10 until size.y.toInt() - 10).random().toDouble()
    )
}