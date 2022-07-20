package agent

import agent.network.Network
import agent.network.Neuron
import environment.Being
import environment.Entity
import environment.Environment
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.max

const val INPUT_OWN_HEALTH = 0
const val INPUT_ANGLE_NEAREST_FOOD = 1
const val INPUT_DIST_NEAREST_FOOD = 2
const val INPUT_ANGLE_NEAREST_BEING = 3
const val INPUT_DIST_NEAREST_BEING = 4
const val INPUT_HEALTH_NEAREST_BEING = 5

const val OUTPUT_SPEED = 0
const val OUTPUT_STEERING = 1

class Agent(val net: Network) {
    var env: Environment? = null
    var ownBeing: Being? = null

    private fun updateInputs() {
        check(env != null) { "Env in agent is null" }

        env?.let { env ->
            net.inputs[INPUT_OWN_HEALTH].activation = ownBeing!!.health / 10.0

            val closestBeing = env.beings.filter { it != ownBeing }.minByOrNull { (it.pos - ownBeing!!.pos).lengthSquared() }

            var closestBeingDist = 2000.0
            var closestBeingAngle = 0.0
            if(closestBeing != null) {
                closestBeingDist = (ownBeing!!.pos - closestBeing.pos).length()
                closestBeingAngle = getRelativeAngleToEntity(closestBeing)
            }

            net.inputs[INPUT_DIST_NEAREST_BEING].activation = (max(0.0, 1000.0 - closestBeingDist) / 1000.0) // TODO: scale ok?
            net.inputs[INPUT_ANGLE_NEAREST_BEING].activation = closestBeingAngle / PI // TODO: scale ok?

            val closestFood = env.foodUnits.minByOrNull { (it.pos - ownBeing!!.pos).lengthSquared() }
            var closestFoodDist = 2000.0
            var closestFoodAngle = 0.0
            if(closestFood != null) {
                closestFoodDist = (ownBeing!!.pos - closestFood.pos).length()
                closestFoodAngle = getRelativeAngleToEntity(closestFood)
            }

            net.inputs[INPUT_DIST_NEAREST_FOOD].activation = (max(0.0, 1000.0 - closestFoodDist) / 1000.0) // TODO: scale ok?
            net.inputs[INPUT_ANGLE_NEAREST_FOOD].activation = closestFoodAngle / PI // TODO: scale ok?
        }
    }

    private fun getRelativeAngleToEntity(entity: Entity): Double {
        val toOther = (entity.pos - ownBeing!!.pos).normalise()
        val ownDirection = ownBeing!!.direction.normalise()
        var relativeAngle = atan2(toOther.y, toOther.x) - atan2(ownDirection.y, ownDirection.x)

        if(relativeAngle > PI) {
            relativeAngle -= 2.0 * PI
        } else if(relativeAngle < -PI) {
            relativeAngle += 2.0 * PI
        }

        return relativeAngle
    }

    fun update(delta: Double) {
        updateInputs()
        net.update()

//        ownBeing!!.orientation -= 2.0 * delta * net.inputs[INPUT_ANGLE_NEAREST_BEING].activation
        ownBeing!!.orientation -= 5.0 * delta * (net.outputs[OUTPUT_STEERING].activation - 0.5) // TODO: what about other activation functions? should they even be allowed for output?

//        ownBeing!!.orientation -= 5.0 * delta * (net.inputs[INPUT_ANGLE_NEAREST_FOOD].activation)

        if(env!!.beings[0] == this.ownBeing) {
            println("${net.inputs[INPUT_ANGLE_NEAREST_FOOD].activation} -> ${net.outputs[OUTPUT_STEERING].activation}")
        }
    }

    fun getOutputs() {
        // TODO: get agent ouput (steering, jump, ...)
    }


    companion object {
        fun initNetworkBoundaries(net: Network) {
            net.inputs.addAll(MutableList(6) { Neuron(net.defaultActivationFunction) })
            net.outputs.addAll(MutableList(2) { Neuron(net.defaultActivationFunction) })
        }
    }
}