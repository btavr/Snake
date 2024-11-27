import pt.isel.canvas.*

data class Snake(val body: List<Position>, val direction: Direction)

const val HEADUP    = "snake|192,0,64,64"
const val HEADRIGHT = "snake|256,0,64,64"
const val HEADDOWN  = "snake|256,64,64,64"
const val HEADLEFT  = "snake|192,64,64,64"
const val TAILUP    = "snake|192,128,64,64"
const val TAILRIGHT = "snake|256,128,64,64"
const val TAILDOWN  = "snake|256,192,64,64"
const val TAILLEFT  = "snake|192,192,64,64"
var bodyParts       = mutableListOf<String>()
var snakeHead       = HEADRIGHT
var snakeTail       = TAILRIGHT


    fun Canvas.drawSnake(snake: Snake) {
        this.erase()
        bodyParts.add(0, snakeHead )
        bodyParts.add(bodyParts.lastIndex, snakeTail)
        this.drawImage(snakeHead, snake.body[0].x * 32, snake.body[0].y * 32,32,32)
        this.drawImage(snakeTail, snake.body[snake.body.lastIndex].x * 32, snake.body[snake.body.lastIndex].y * 32,32,32)
    }

    fun Snake.move(): Snake {
        val newHead = nextHeadPosition()
        return Snake(listOf(newHead) + body.dropLast(1), direction)
    }

    fun Snake.changeDirection(newDirection: Direction): Snake {
        when (newDirection) {
            Direction.UP -> {
                snakeHead = HEADUP
                snakeTail = TAILUP
            }
            Direction.DOWN -> {
                snakeHead = HEADDOWN
                snakeTail = TAILDOWN
            }
            Direction.LEFT -> {
                snakeHead = HEADLEFT
                snakeTail = TAILLEFT
            }
            Direction.RIGHT -> {
                snakeHead = HEADRIGHT
                snakeTail = TAILRIGHT
            }
        }
        return Snake(body, newDirection)
    }

fun Snake.nextHeadPosition(): Position {
    val head = body.first()
    val movement = when (direction) {
        Direction.UP -> Position(0, -1)
        Direction.DOWN -> Position(0, 1)
        Direction.LEFT -> Position(-1, 0)
        Direction.RIGHT -> Position(1, 0)
    }
    return (head + movement).wrap() // Soma a posição de movimento e aplica o wrap
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