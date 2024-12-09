import pt.isel.canvas.*

data class Snake(val body: List<Position>, val direction: Direction, val toGrow: Int = 0)

fun Canvas.drawSnake(snake: Snake) {
    this.erase()
    val headImage = when (snake.direction) {
        Direction.UP -> "snake|192,0,64,64"
        Direction.DOWN -> "snake|256,64,64,64"
        Direction.LEFT -> "snake|192,64,64,64"
        Direction.RIGHT -> "snake|256,0,64,64"
    }
    val tailImage = when (getTailDirection(snake)) {
        Direction.UP -> "snake|192,128,64,64"
        Direction.DOWN -> "snake|256,192,64,64"
        Direction.LEFT -> "snake|192,192,64,64"
        Direction.RIGHT -> "snake|256,128,64,64"
    }
    this.drawImage(headImage, snake.body.first().x * 32, snake.body.first().y * 32, 32, 32)
    this.drawImage(tailImage, snake.body.last().x * 32, snake.body.last().y * 32, 32, 32)
}

fun getTailDirection(snake: Snake): Direction {
    // Determine the direction of the tail based on the last two segments
    if (snake.body.size < 2) return snake.direction // Default to current direction if not enough segments

    return snake.direction
//    val tailEnd = snake.body.last()
//    val beforeTailEnd = snake.body[snake.body.size - 2]
//
//    return when {
//        tailEnd.x < beforeTailEnd.x -> Direction.RIGHT
//        tailEnd.x > beforeTailEnd.x -> Direction.LEFT
//        tailEnd.y < beforeTailEnd.y -> Direction.DOWN
//        else -> Direction.UP
//    }
}

fun Snake.move(): Snake {
    val newHead = nextHeadPosition()
    return Snake(listOf(newHead) + body.dropLast(1), direction)
}

fun Snake.changeDirection(newDirection: Direction): Snake {
    return if (newDirection != direction.opposite()) {
        Snake(body, newDirection)
    } else {
        this
    }
}

fun Snake.nextHeadPosition(): Position {
    val head = body.first()
    val movement = when (direction) {
        Direction.UP -> Position(0,-1)
        Direction.DOWN -> Position(0, 1)
        Direction.LEFT -> Position(-1, 0)
        Direction.RIGHT -> Position(1, 0)
    }
    return (head + movement).wrap()
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}