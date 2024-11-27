import pt.isel.canvas.*
const val WIDTH      = 640
const val HEIGHT     = 512
const val XSTART     = 10
const val YSTART     = 8
const val SNAKESPEED = 250
const val WALLSPEED  = 5000
const val CEL        = 32

data class Game(val snake: Snake, val wall: List<Position>)

fun main() {
    onStart {
        val arena = Canvas(WIDTH, HEIGHT, YELLOW) // 20x16 células de 32px
        val snake = Snake(listOf(Position(XSTART, YSTART), Position(XSTART - 1, YSTART)), Direction.RIGHT)
        var game = Game(snake, wall = emptyList())

        arena.onKeyPressed { key ->
            val oldDirection = game.snake.direction
            val newDirection = when (key.code) {
                UP_CODE -> Direction.UP
                DOWN_CODE -> Direction.DOWN
                LEFT_CODE -> Direction.LEFT
                RIGHT_CODE -> Direction.RIGHT
                else -> game.snake.direction
            }
            if (newDirection != game.snake.direction.opposite()) {
                val newSnakeDirection = game.snake.changeDirection(newDirection)
                if (newSnakeDirection.nextHeadPosition() in game.wall){
                    game.snake.changeDirection(oldDirection)
                } else{
                    game = game.copy(snake = newSnakeDirection)
                }
            }
        }

        arena.onTimeProgress(SNAKESPEED) {
            val nextPosition = game.snake.nextHeadPosition()
            val newSnake = if (nextPosition in game.wall) {
                game.snake
            } else {
                game.snake.move()
            }
            val newWall = if (it / WALLSPEED > game.wall.size) {
                generateBrick(game)
            } else {
                game.wall
            }
            game = Game(newSnake, newWall)
            drawGame(arena, game)
        }
    }
    onFinish {}
}

fun drawGame(arena: Canvas, game: Game) {
    arena.erase()
    // Desenhar cobra
    for (segment in game.snake.body) {
        arena.drawSnake(game.snake)
    }
    // Desenhar tijolos
    for (brick in game.wall) {
        arena.drawImage("brick.png", brick.x * CEL, brick.y * CEL,CEL,CEL)
    }
}

fun generateBrick(game: Game): List<Position> {
    val totalCells = (0 until 20).flatMap { x -> (0 until 16).map { y -> Position(x, y) } }
    val freeCells = totalCells.filter { it !in game.snake.body && it !in game.wall }
//       val freeCells = (0 until 20).flatMap { x ->
//        (0 until 16).map { y -> Position(x, y) }
//    }.filter { it !in game.snake.body && it !in game.wall }

    return if (freeCells.isNotEmpty()) {
        game.wall + freeCells.random()
    } else game.wall
}