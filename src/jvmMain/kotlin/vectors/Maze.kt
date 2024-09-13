package vectors

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Cell(val x: Int, val y: Int)

data class Maze(val grid: List<List<Boolean>>, val start: Cell, val end: Cell)

@Composable
fun MazeSolver() {
    val maze = remember {
        // Example maze (replace with your actual maze data)
        generateMaze(30, 20)
//        Maze(
//            grid = listOf(
//                listOf(true, true, true, true, true),
//                listOf(true, false, false, false, true),
//                listOf(true, true, true, false, true),
//                listOf(true, false, false, false, true),
//                listOf(true, true, true, true, true)
//            ),
//            start = Cell(1, 1),
//            end = Cell(3, 3)
//        )
    }

    var path by remember { mutableStateOf<List<Cell>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var currentPosition by remember { mutableStateOf<Cell?>(null) }
    var previousPosition by remember { mutableStateOf<Cell?>(null) }
    var stepCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        path = solveMaze(maze)

        scope.launch {
            for (i in path.indices) {
                previousPosition = currentPosition
                currentPosition = path[i]
                stepCount++ // Increment step count with each move
                delay(250)
            }
            previousPosition = currentPosition
            currentPosition = null
            stepCount = 0 // Reset step count after animation
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellSize = size.minDimension / maze.grid.size
        val cornerRadius = cellSize * 0.2f
        val personColor = Color.White

        // Draw grid and walls
        for (y in maze.grid.indices) {
            for (x in maze.grid[y].indices) {
                if (maze.grid[y][x]) { // If it'sa wall cell
                    val topLeft = Offset(x * cellSize, y * cellSize)
                    val bottomRight = Offset((x + 1) * cellSize, (y + 1) * cellSize)

                    // Check neighbors to determine corner rounding
                    val roundTopLeft = shouldRoundCorner(maze, x, y, -1, -1)
                    val roundTopRight = shouldRoundCorner(maze, x, y, 1, -1)
                    val roundBottomLeft = shouldRoundCorner(maze, x, y, -1, 1)
                    val roundBottomRight = shouldRoundCorner(maze, x, y, 1, 1)

                    val path = Path().apply {
                        moveTo(topLeft.x + if (roundTopLeft) cornerRadius else 0f, topLeft.y)
                        lineTo(bottomRight.x - if (roundTopRight) cornerRadius else 0f, topLeft.y)
                        if (roundTopRight) {
                            arcToRad(
                                rect = androidx.compose.ui.geometry.Rect(
                                    left = bottomRight.x - 2 * cornerRadius,
                                    top = topLeft.y,
                                    right = bottomRight.x,
                                    bottom = topLeft.y + 2 * cornerRadius
                                ),
                                startAngleRadians = (3 * Math.PI / 2).toFloat(),
                                sweepAngleRadians = (Math.PI / 2).toFloat(),
                                forceMoveTo = false
                            )
                        }
                        lineTo(bottomRight.x, bottomRight.y - if (roundBottomRight) cornerRadius else 0f)
                        if (roundBottomRight) {
                            arcToRad(
                                rect = androidx.compose.ui.geometry.Rect(
                                    left = bottomRight.x - 2 * cornerRadius,
                                    top = bottomRight.y - 2 * cornerRadius,
                                    right = bottomRight.x,
                                    bottom = bottomRight.y
                                ),
                                startAngleRadians = 0f,
                                sweepAngleRadians = (Math.PI / 2).toFloat(),
                                forceMoveTo = false
                            )
                        }
                        lineTo(topLeft.x + if (roundBottomLeft) cornerRadius else 0f, bottomRight.y)
                        if (roundBottomLeft) {
                            arcToRad(
                                rect = androidx.compose.ui.geometry.Rect(
                                    left = topLeft.x,
                                    top = bottomRight.y - 2 * cornerRadius,
                                    right = topLeft.x + 2 * cornerRadius,
                                    bottom = bottomRight.y
                                ),
                                startAngleRadians = (Math.PI / 2).toFloat(),
                                sweepAngleRadians = (Math.PI / 2).toFloat(),
                                forceMoveTo = false
                            )
                        }
                        lineTo(topLeft.x, topLeft.y + if (roundTopLeft) cornerRadius else 0f)
                        if (roundTopLeft) {
                            arcToRad(
                                rect = androidx.compose.ui.geometry.Rect(
                                    left = topLeft.x,
                                    top = topLeft.y,
                                    right = topLeft.x + 2 * cornerRadius,
                                    bottom = topLeft.y + 2 * cornerRadius
                                ),
                                startAngleRadians = Math.PI.toFloat(),
                                sweepAngleRadians = (Math.PI / 2).toFloat(),
                                forceMoveTo = false
                            )
                        }
                        close()
                    }

                    drawPath(path, color = Color.Black)
                }
            }
        }

        // Draw current position
        currentPosition?.let { cell ->
            val center = Offset((cell.x + 0.5f) * cellSize,
                (cell.y + 0.5f) * cellSize
            )
            val cp = currentPosition
            val pp = previousPosition
            val direction = when {
                (cp?.x ?: 0) > (pp?.x ?: 0) -> Direction.RIGHT
                (cp?.x ?: 0) < (pp?.x ?: 0) -> Direction.LEFT
                (cp?.y ?: 0) > (pp?.y ?: 0) -> Direction.DOWN
                else -> Direction.UP
            }

            drawPerson(center, cellSize * 0.4f, stepCount, personColor, direction)

        }

        // Mark start and end
        drawCircle(Color.Green, cellSize * 0.3f, Offset((maze.start.x + 0.5f) * cellSize, (maze.start.y + 0.5f) * cellSize))
        drawCircle(Color.Blue, cellSize * 0.3f, Offset((maze.end.x + 0.5f) * cellSize, (maze.end.y + 0.5f) * cellSize))
    }
}

fun solveMaze(maze: Maze): List<Cell> {val queue = ArrayDeque<Cell>()
    val visited = mutableSetOf<Cell>()
    val parents = mutableMapOf<Cell, Cell>()

    queue.add(maze.start)
    visited.add(maze.start)

    while (queue.isNotEmpty()) {val current = queue.removeFirst()

        if (current == maze.end) {
            return reconstructPath(parents, maze.start, maze.end)
        }

        for (neighbor in getNeighbors(maze, current)) {
            if (neighbor !in visited) {
                queue.add(neighbor)
                visited.add(neighbor)
                parents[neighbor] = current
            }
        }
    }

    return emptyList() // No solution found
}

// Helper function to get valid neighbors of a cell
private fun getNeighbors(maze: Maze, cell: Cell): List<Cell> {
    val neighbors = mutableListOf<Cell>()
    val (x, y) = cell

    // Check neighbors in all four directions
    if (x > 0 && !maze.grid[y][x - 1]) neighbors.add(Cell(x - 1, y))
    if (x < maze.grid[0].size - 1 && !maze.grid[y][x + 1]) neighbors.add(Cell(x + 1, y))
    if (y > 0 && !maze.grid[y - 1][x]) neighbors.add(Cell(x, y - 1))
    if (y < maze.grid.size - 1 && !maze.grid[y + 1][x]) neighbors.add(Cell(x, y + 1))

    return neighbors
}

// Helper function to reconstruct the path from start to end
private fun reconstructPath(parents: Map<Cell, Cell>, start:Cell,  end: Cell): List<Cell> {
    val path = mutableListOf<Cell>()
    var current = end

    while (current in parents) {
        path.add(0, current)
        current = parents[current]!!
    }

    path.add(0, start) // Add the starting cell
    return path
}

fun generateMaze(width: Int, height: Int): Maze {
    // Initialize grid with all walls
    val grid = MutableList(height) { MutableList(width) { true } }

    // Recursive backtracker algorithm
    fun carvePassages(cell: Cell) {
        grid[cell.y][cell.x] = false // Mark current cell as visited (not a wall)

        val directions = listOf(
            Cell(0, -2), // Up
            Cell(2, 0), // Right
            Cell(0, 2), // Down
            Cell(-2, 0) // Left
        ).shuffled() // Randomize directions

        for (direction in directions) {
            val nextCell = Cell(cell.x + direction.x, cell.y+ direction.y)
            val wallBetween = Cell(cell.x + direction.x / 2, cell.y + direction.y / 2)

            if (nextCell.x in 0 until width && nextCell.y in 0 until height && grid[nextCell.y][nextCell.x]) {
                grid[wallBetween.y][wallBetween.x] = false // Carve passage through wall
                carvePassages(nextCell)
            }
        }
    }

    // Set extreme points as walls
    for (x in 0 until width) {
        grid[0][x] = true // Top row
        grid[height - 1][x] = true // Bottom row
    }
    for (y in 0 until height) {
        grid[y][0] = true // Left column
        grid[y][width - 1] = true // Right column
    }


    // Choose a random starting cell
    val startCell = Cell(Random.nextInt(width), Random.nextInt(height))
    carvePassages(startCell)

    // Choose a random end cell (far from the start)
    var endCell: Cell
    do {
        endCell = Cell(Random.nextInt(width), Random.nextInt(height))
    } while (grid[endCell.y][endCell.x]==true || manhattanDistance(startCell, endCell) < (width + height) / 4)

    return Maze(grid, startCell, endCell)
}

private fun manhattanDistance(cell1: Cell,cell2: Cell): Int {
    return kotlin.math.abs(cell1.x - cell2.x) + kotlin.math.abs(cell1.y - cell2.y)
}

// Helper function to determine if a corner should be rounded
private fun shouldRoundCorner(maze: Maze, x: Int, y: Int, dx: Int, dy: Int): Boolean {
    val nextX = x + dx
    val nextY = y + dy
    if (nextX in maze.grid[0].indices && nextY in maze.grid.indices) {
        // Round corner only if neighbor is not a wall AND there's a turn
        return !maze.grid[nextY][nextX] && isTurn(maze, x, y, dx, dy)
    }
    return false
}

// Helper function to determine if there's a turn at a corner
private fun isTurn(maze: Maze, x: Int, y: Int, dx: Int, dy: Int): Boolean {
    val neighborX = x + dx
    val neighborY = y + dy

    // Check if the neighbor is a passage
    if (neighborX in maze.grid[0].indices && neighborY in maze.grid.indices && !maze.grid[neighborY][neighborX]) {
        // Check if there's a wall in BOTH perpendicular directions to indicate a turn
        val perpendicularX1 = x + dy
        val perpendicularY1 = y + dx
        val perpendicularX2 = x - dy
        val perpendicularY2 = y - dx
        if (perpendicularX1 in maze.grid[0].indices && perpendicularY1 in maze.grid.indices &&
            perpendicularX2 in maze.grid[0].indices && perpendicularY2 in maze.grid.indices
        ) {
            return maze.grid[perpendicularY1][perpendicularX1] && maze.grid[perpendicularY2][perpendicularX2]
        }
    }
    return false
}

private fun DrawScope.drawPerson(position: Offset, size: Float, stepCount: Int, personColor: Color, direction: Direction) {
    val legSwing = (size * 0.3f * Math.sin(stepCount *0.5 * Math.PI)).toFloat()
    val armSwing = (size * 0.2f * Math.cos(stepCount * 0.5 * Math.PI)).toFloat()

    // Calculate arm and leg offsets based on direction
    val (legOffset1, legOffset2, armOffset1, armOffset2) = when (direction) {
        Direction.UP -> listOf(
            Offset(-size * 0.2f, size + legSwing),
            Offset(size * 0.2f, size - legSwing),
            Offset(-size *0.3f, size * 0.6f + armSwing),
            Offset(size * 0.3f, size * 0.6f - armSwing)
        )
        Direction.DOWN -> listOf(
            Offset(size * 0.2f, size + legSwing),
            Offset(-size * 0.2f, size - legSwing),
            Offset(size * 0.3f, size * 0.6f + armSwing),
            Offset(-size * 0.3f, size * 0.6f - armSwing)
        )
        Direction.LEFT -> listOf(
            Offset(-size * 0.2f, size + legSwing),
            Offset(size * 0.2f, size - legSwing),
            Offset(-size *0.3f, size * 0.6f + armSwing),
            Offset(size * 0.3f, size * 0.6f - armSwing)
        )
        Direction.RIGHT -> listOf(
            Offset(size * 0.2f, size + legSwing),
            Offset(-size * 0.2f, size - legSwing),
            Offset(size * 0.3f, size * 0.6f + armSwing),
            Offset(-size * 0.3f, size * 0.6f - armSwing)
        )
    }

    // Draw head
    drawCircle(
        color = personColor,
        radius = size * 0.3f,
        center = position
    )

    // Draw body
    drawLine(
        color = personColor,
        start = Offset(position.x, position.y + size * 0.3f),
        end = Offset(position.x, position.y + size * 0.8f),
        strokeWidth = size * 0.1f
    )

    // Draw legs with striding animation
    drawLine(
        color = personColor,
        start = Offset(position.x, position.y + size * 0.8f),
        end = position + legOffset1,
        strokeWidth = size * 0.1f
    )
    drawLine(
        color = personColor,
        start = Offset(position.x, position.y + size * 0.8f),
        end = position + legOffset2,
        strokeWidth = size * 0.1f
    )

    // Draw arms with swinging animation
    drawLine(
        color = personColor,
        start = Offset(position.x, position.y + size * 0.4f),
        end = position + armOffset1,
        strokeWidth = size * 0.1f
    )
    drawLine(
        color = personColor,
        start = Offset(position.x, position.y + size * 0.4f),
        end = position + armOffset2,
        strokeWidth = size * 0.1f
    )

    // Draw bag
    val bagOffset = when (direction) {
        Direction.UP -> Offset(size * 0.2f, size * 0.7f)
        Direction.DOWN -> Offset(-size * 0.2f, size * 0.7f)
        Direction.LEFT -> Offset(size * 0.7f, -size * 0.2f)
        Direction.RIGHT -> Offset(size * 0.7f, size * 0.2f)
    }
    val bagPosition = position + bagOffset
    drawRect(
        color = Color(0xFfAB2040),
        topLeft = bagPosition,
        size = Size(size * 0.2f, size * 0.3f)
    )
}

// Enum for directions
enum class Direction { UP, DOWN, LEFT, RIGHT }