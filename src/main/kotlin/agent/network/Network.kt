package agent.network

class Network(val defaultActivationFunction: ActivationFunction = SigmoidActivation()) {
    val inputs = ArrayList<Neuron>()
    val outputs = ArrayList<Neuron>()
    val hidden = ArrayList<Neuron>()
    val synapses = ArrayList<Synapse>()
    val nonInputNeurons get() = hidden.union(outputs)
    val nonOutputNeurons get() = hidden.union(inputs)
    val neurons get() = inputs.union(outputs).union(hidden)

    // fire all neurons independently, can only progress one step further
    fun update() {
        val inputsByNeuron: MutableMap<Neuron, MutableList<Synapse>> = nonInputNeurons.associateWith { ArrayList<Synapse>() }.toMutableMap()
        synapses.forEach { inputsByNeuron[it.target]?.add(it) }

        val activationsByNeuron = neurons.associateWith { it.activation }

        nonInputNeurons.forEach { neuron ->
            val stimulus = inputsByNeuron[neuron]?.sumByDouble { (activationsByNeuron[it.source] ?: 0.0) * it.weight } ?: 0.0
            neuron.setActivationBasedOnStimulus(stimulus)
        }
    }

    fun getSynapsesTo(target: Neuron) = synapses.filter { it.target == target }
    fun getSynapsesFrom(source: Neuron) = synapses.filter { it.source == source }

    fun clone(): Network {
        val net = Network(this.defaultActivationFunction)

        val clonedNeurons = this.neurons.associateWith { it.clone() }

        this.inputs.forEach { clonedNeurons[it]?.let { net.inputs.add(it) } }
        this.outputs.forEach { clonedNeurons[it]?.let { net.outputs.add(it) } }
        this.hidden.forEach { clonedNeurons[it]?.let { net.hidden.add(it) } }
        this.synapses.forEach { s -> clonedNeurons[s.source]?.let { source -> clonedNeurons[s.target]?.let { target -> net.synapses.add(Synapse(source, target, s.weight)) }} }

        return net
    }

}