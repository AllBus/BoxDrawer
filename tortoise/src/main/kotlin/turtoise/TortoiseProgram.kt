package turtoise

data class TortoiseProgram(
    val commands: List<TortoiseAlgorithm>,
    val algorithms: Map<String, TortoiseAlgorithm>
)

