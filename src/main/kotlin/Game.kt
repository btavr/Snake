import pt.isel.canvas.*
// Neste ficheiro econtra-se toda a logica do jogo e os seus elementos
data class Game(val snake: Snake, val wall: List<Position>, val apple: Position?, val score: Int, val elapsedTime: Long, val dynamicWall: List<Position>)

fun main() {
    onStart {
        val arena = Canvas(640, 512, YELLOW) // 20x16 células de 32px
        val snake = Snake(listOf(Position(10, 8), Position(10 - 1, 8), Position(10 - 2, 8), Position(10 - 3, 8), Position(10 - 4, 8)), Direction.RIGHT, toGrow = 0)
        var game = Game(snake, initialBricks(), apple = null, 0, 0L, dynamicWall = emptyList())

        game = game.copy(
            apple = generateApple(game)
        )
        //Logica para mudancas de direcao atravez das setas do teclado
        arena.onKeyPressed { key ->
            val oldDirection = game.snake.direction
            val newDirection = when (key.code) {
                UP_CODE -> Direction.UP
                DOWN_CODE -> Direction.DOWN
                LEFT_CODE -> Direction.LEFT
                RIGHT_CODE -> Direction.RIGHT
                else -> game.snake.direction
            }
            // Muda a direcao da cobra
            if (newDirection != game.snake.direction.opposite()) {
                val newSnakeDirection = game.snake.changeDirection(newDirection)
                if (newSnakeDirection.nextHeadPosition() in game.wall){
                    game.snake.changeDirection(oldDirection)
                } else{
                    game = game.copy(snake = newSnakeDirection)
                }
            }
        }

        arena.onTimeProgress(200) { elapsed ->
            // Caso a cobra nao se possa mexer o jogo termina
            // Se a cobra for >= 60, ganha o jogo
            // Se a cobra for < 60, perde o jogo
            if (!canMove(game)) {
                if (game.snake.body.size >= 60) {
                    arena.drawText(140, 250, "GAME OVER", GREEN, 60)
                    arena.drawText(525, 505, "You win!", GREEN, 25)
                } else {
                    arena.drawText(140, 250, "GAME OVER", RED, 60)
                    arena.drawText(525, 505, "You lose!", RED, 25)
                }
                return@onTimeProgress
            }   // Print game over a verde caso vitoria e a vermelho caso derrota
                // Atualiza a barra de estados com You win ou You lose
            val nextPosition = game.snake.nextHeadPosition()
            var newSnake = if (nextPosition in game.wall || nextPosition in game.dynamicWall || nextPosition in game.snake.body) {
                game.snake // Cobra colidiu com um tijolo ou consigo mesma
            } else {
                game.snake.move()
            }

            // Adicionar novos tijolos a cada 5000ms
            val newDynamicWall = if (elapsed / 5000 > game.dynamicWall.size) {
                generateBrick(game)
            } else {
                game.dynamicWall
            }
            var newScore = game.score
            // Quando a cobra come a maca acrescenta +1 as variaveis toGrow e score
            val newApple = if (game.apple != null && nextPosition == game.apple) {
                 newScore += 1
                 newSnake = newSnake.copy(toGrow = newSnake.toGrow + 1)
                generateApple(game)
            } else {
                game.apple
            }
            // Atualiza o jogo e os seus elementos
            game = game.copy(
                snake = newSnake,
                dynamicWall = newDynamicWall,
                apple = newApple,
                score = newScore,
                elapsedTime = elapsed // Atualiza o tempo decorrido
            )

            drawGame(arena, game)
        }
    }
    onFinish {}
}
 // desenha o jogo com todos os seus elementos
fun drawGame(arena: Canvas, game: Game) {
    arena.erase()
    // Desenhar cobra
    for (segment in game.snake.body) {
        arena.drawSnake(game.snake)
    }
    // Desenhar tijolos
    arena.drawBricks(game.wall)
    arena.drawBricks(game.dynamicWall)
    // Desenha maca
    arena.drawApple(game)

    // Desenha barra de estados
    arena.drawRect(0, 480, arena.width, 64, BLUE)
    // Desenha componentes da barra de estados
    val timeInSeconds = (game.elapsedTime / 1000).toInt()
    arena.drawText(10, 505, "Size: ${game.snake.body.size}", WHITE, 25)
    arena.drawText(150, 505, "Score: ${game.score}", WHITE,25)
    arena.drawText(300, 505, "Time: $timeInSeconds s", WHITE,25)
}

// Verifica se a cobra se pode mover ou se esta presa.
// Verifica se as possiveis possicoes, LEFT, RIGHT, UP E DOWN, ja se encontram ocupadas com bricks ou com o corpo da cobra
//
fun canMove(game: Game): Boolean {
    // Define as proximas posicoes possiveis para a cabeca da cobra com base na sua localizacao atual
    val possiblePositions = listOf(
        game.snake.body.first() + Position(0, -1),  // Move UP
        game.snake.body.first() + Position(0, 1),   // Move DOWN
        game.snake.body.first() + Position(-1, 0),  // Move LEFT
        game.snake.body.first() + Position(1, 0)    // Move RIGHT
    ).map { it.wrap() }
    return   possiblePositions.any { nextPosition ->
        nextPosition !in game.snake.body &&
                nextPosition !in game.wall &&
                nextPosition !in game.dynamicWall
    }   // Verifica que existe uma possicao livre e que a cobra se pode mexer
}
