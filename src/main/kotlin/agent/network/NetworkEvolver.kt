package agent.network

import java.util.*
import kotlin.math.max
import kotlin.math.min

class NetworkEvolver(val neuronLimit: Int = 300, val allowNegativeWeights: Boolean = true, val synapseWeightLimit: Double = 2.0, val synapseLimit: Int = 1000) {

    val random = Random()

    fun evolve(net: Network) {
        if(random.nextDouble() < 0.85) {
            evolveSynapseWeights(net, count = net.synapses.size / 3)
        }

        if(random.nextDouble() < 0.40 && net.hidden.size > neuronLimit / 4) {
            removeNeurons(net, net.hidden.size / 10)
        }

        if(random.nextDouble() < 0.70 && net.hidden.size < neuronLimit) {
            addNeurons(net, max(neuronLimit / 100, net.hidden.size / 10))
        }

        if(random.nextDouble() < 0.40 && net.synapses.size > synapseLimit / 4) {
            removeSynapses(net, net.synapses.size / 10)
        }

        if(random.nextDouble() < 0.70 && net.synapses.size < synapseLimit) {
            addSynapses(net, min(net.hidden.size / 4, synapseLimit / 10))
        }

        // todo: neuron types
    }

    fun evolveSynapseWeights(net: Network, count: Int, changeMean: Double = 0.0, changeDeviation: Double = 1.0) {
        val evolvingSynapses = ArrayList<Synapse>()
        while(evolvingSynapses.size < count && evolvingSynapses.size < net.synapses.size) {
            evolvingSynapses.add(net.synapses.filter { it !in evolvingSynapses }.random())
        }

        evolvingSynapses.forEach { synapse ->
            val weightChange = random.nextGaussian() * changeDeviation + changeMean
            synapse.weight = max(-synapseWeightLimit, min(synapseWeightLimit, synapse.weight + weightChange))
            if(!allowNegativeWeights) {
                synapse.weight = max(0.0, synapse.weight)
            }
        }
    }

    // tends towards unused neurons
    fun addSynapses(net: Network, count: Int, weightMean: Double = 0.3, weightDeviation: Double = 0.5, fixedSource: Neuron? = null, fixedTarget: Neuron? = null) {
        val sourceSpace = net.hidden.filter { it !in net.outputs }
        val targetSpace = net.hidden.filter { it !in net.inputs }

        repeat(count) {
            val source = fixedSource ?: sourceSpace.randomOrNull()
            val target = fixedTarget ?: targetSpace.filter { it != source }.randomOrNull()

            if (source != null) {
                if(target != null) {
                    if(net.synapses.none { it.source == source && it.target == target }) {
                        val newSynapse = Synapse(source, target, random.nextGaussian() * weightDeviation + weightMean)
                        net.synapses.add(newSynapse)
                    }
                }
            }
        }
    }

    // TODO: tend towards unused ones
    fun removeSynapses(net: Network, count: Int) {
        repeat(count) {
            val synapse = net.synapses.randomOrNull()
            synapse?.let { net.synapses.remove(it) }
        }
    }

    fun addNeurons(net: Network, count: Int) {
        repeat(count) {
            val neuron = Neuron(net.defaultActivationFunction)
            net.hidden.add(neuron)

            addSynapses(net, 1, fixedTarget = neuron)
            addSynapses(net, 1, fixedSource = neuron)
        }
    }

    fun removeNeurons(net: Network, count: Int) {
        repeat(count) {
            val removedNeuron = net.hidden.filter { it !in net.inputs && it !in net.outputs }.random()
            val removedSynapses = net.synapses.filter { it.source == removedNeuron || it.target == removedNeuron }

            net.hidden.remove(removedNeuron)
            net.synapses.removeAll(removedSynapses)
        }
    }
}