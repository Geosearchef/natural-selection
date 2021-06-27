package agent.network

class Neuron(val activationFunction: ActivationFunction) {
    var activation: Double = 0.0

    fun setActivationBasedOnStimulus(stimulus: Double) {
        activation = activationFunction.getActivation(stimulus)
    }

    fun clone() = Neuron(activationFunction)
}