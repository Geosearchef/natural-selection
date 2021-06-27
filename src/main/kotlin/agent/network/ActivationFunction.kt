package agent.network

abstract class ActivationFunction {
    abstract fun getActivation(stimulus: Double): Double
}

class StepActivation(val threshold: Double) : ActivationFunction() {
    override fun getActivation(stimulus: Double) = if(stimulus > threshold) 1.0 else 0.0
}

class LinearActivation() : ActivationFunction() {
    override fun getActivation(stimulus: Double) = stimulus
}

class ReLUActivation(val threshold: Double = 0.0, val scale: Double = 1.0) : ActivationFunction() {
    override fun getActivation(stimulus: Double) = if(stimulus > threshold) (stimulus - threshold) * scale else 0.0
}

class LeakyReLUActivation(val threshold: Double = 0.0, val scale: Double = 1.0, val leakyScale: Double = 0.1) : ActivationFunction() {
    override fun getActivation(stimulus: Double) = (stimulus - threshold) * (if(stimulus > threshold) scale else leakyScale)
}