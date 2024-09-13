package vectors

import kotlin.math.pow
import java.util.*

class SimpleMain{

}



data class Neighbor(val node: Node, val weight: Int)
data class Node(val id: Int, val neighbors: MutableList<Neighbor> = mutableListOf()){
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (this === other)
    }

    override fun toString(): String {
        return "Node($id)"
    }
}

fun dijkstra(graph: List<Node>, start: Node, end: Node): List<Node> {
    val distances = mutableMapOf<Node, Int>().withDefault { Int.MAX_VALUE }
    val previous= mutableMapOf<Node, Node?>()
    val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })

    distances[start] = 0
    queue.offer(start to 0)

    while (queue.isNotEmpty()) {
        val (current, dist) = queue.poll()
        if (current == end) break

        // Iterate through neighbors using the Neighbor class
        for (neighbor in current.neighbors) {
            val newDistance = dist + neighbor.weight
            if (newDistance < distances.getValue(neighbor.node)){
                distances[neighbor.node] = newDistance
                previous[neighbor.node] = current
                queue.offer(neighbor.node to newDistance)
            }
        }
    }

    // Reconstruct path
    val path = mutableListOf<Node>()
    var node: Node? = end
    while (node != null) {
        path.add(node)
        node = previous[node]
    }
    return path.reversed()
}

fun createRandomGraph(numNodes: Int, edgeProbability: Double): List<Node> {
    val nodes = (0 until numNodes).map { Node(it) }
    val random = Random()

    for (i in 0 until numNodes) {
        for(j in i + 1 until numNodes) {
            if (random.nextDouble() < edgeProbability) {
                val weight = random.nextInt(20) + 1 // Random weight between 1 and 10
                nodes[i].neighbors.add(Neighbor(nodes[j] , weight))
                nodes[j].neighbors.add(Neighbor(nodes[i] , weight)) // Assuming undirected graph
            }
        }
    }

    return nodes
}

fun sinTaylor(x: Double, terms: Int): Double {
    var result = 0.0
    for (n in 0 until terms) {
        val term = (-1.0).pow(n) * x.pow(2 * n + 1) / factorial(2 * n + 1)
        result += term
    }
    return result
}

fun factorial(n: Int): Double {
    var result = 1.0
    for (i in 2..n) {
        result *= i
    }
    return result
}



fun maina() {
    val angleDegrees = 60.0
    val angleRadians = Math.toRadians(angleDegrees)
    val numTerms = 10

    val sinValue = sinTaylor(angleRadians, numTerms)
    println("sin($angleDegrees degrees) = $sinValue")

    val graph = createRandomGraph(20, 0.3)
    for (node in graph) {
        println("Node ${node.id}:")
        for ((neighbor, weight) in node.neighbors) {
            println("  -> Node ${neighbor.id} (weight $weight)")
        }
    }
    println("start")
    val result = dijkstra(graph, graph.first() , graph.last())
    result.forEach { println(it) }
}