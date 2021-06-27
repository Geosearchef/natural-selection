package agent.network

class Network(val defaultActivationFunction: ActivationFunction = ReLUActivation()) {
    val inputs = ArrayList<Neuron>()
    val outputs = ArrayList<Neuron>()
    val neurons = ArrayList<Neuron>()
    val synapses = ArrayList<Synapse>()

    // fire all neurons independently, can only progress one step further
    fun update() {
        val inputsByNeuron: MutableMap<Neuron, MutableList<Synapse>> = neurons.map { it to ArrayList<Synapse>() }.toMap().toMutableMap()
        synapses.forEach { inputsByNeuron[it.target]?.add(it) }

        val activationsByNeuron = neurons.map { it to it.activation }.toMap()

        neurons.forEach { neuron ->
            val stimulus = inputsByNeuron[neuron]?.sumByDouble { (activationsByNeuron[it.source] ?: 0.0) * it.weight } ?: 0.0
            neuron.setActivationBasedOnStimulus(stimulus)
        }
    }

    fun clone(): Network {
        val net = Network(this.defaultActivationFunction)

        val clonedNeurons = this.neurons.map { it to it.clone() }.toMap()
        net.neurons.addAll(clonedNeurons.values)

        this.inputs.forEach { clonedNeurons[it]?.let { net.inputs.add(it) } }
        this.outputs.forEach { clonedNeurons[it]?.let { net.outputs.add(it) } }
        this.synapses.forEach { s -> clonedNeurons[s.source]?.let { source -> clonedNeurons[s.target]?.let { target -> net.synapses.add(Synapse(source, target, s.weight)) }} }

        return net
    }

}