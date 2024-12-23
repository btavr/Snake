import pt.isel.canvas.*
// Este ficheiro descreve a logica da classe snake e das suas funcoes
data class Snake(val body: List<Position>, val direction: Direction, val toGrow: Int = 0)

fun Canvas.drawSnake(snake: Snake) {
    this.erase()
    // Desenha a cabeca da cobra com base na direcao atual
    val headImage = when (snake.direction) {
        Direction.UP -> "snake|192,0,64,64"
        Direction.DOWN -> "snake|256,64,64,64"
        Direction.LEFT -> "snake|192,64,64,64"
        Direction.RIGHT -> "snake|256,0,64,64"
    }
    this.drawImage(headImage, snake.body.first().x * 32, snake.body.first().y * 32, 32, 32)
    // Desenha o corpo da cobra
    // Por cada elemento presente no corpo da cobra determina a direcao do elemento anterior e do elemento seguinte para escolher a imagem certa para o elemento atual
    for (i in 1 until snake.body.size - 1) {
        val prevDirection = getPrevSegmentDirection(snake, i)
        val nextDirection = getNextSegmentDirection(snake, i)
        val segmentImage = getSegmentImage(prevDirection, nextDirection)
        this.drawImage(segmentImage, snake.body[i].x * 32, snake.body[i].y * 32, 32, 32)
    }
    // Desenha a cauda da cobra com base na direcao atual
    val tailImage = when (getTailDirection(snake)) {
        Direction.UP -> "snake|192,128,64,64"
        Direction.DOWN -> "snake|256,192,64,64"
        Direction.LEFT -> "snake|192,192,64,64"
        Direction.RIGHT -> "snake|256,128,64,64"
    }
    this.drawImage(tailImage, snake.body.last().x * 32, snake.body.last().y * 32, 32, 32)
}

// Define a direcao da cauda para escolher a imagem certa na funcao drawSnake
// Compara as coordenadas do elemento cauda e do elemento anterior a cauda
// Define casos especificos para quando a cobra esta a passar pelos limites da arena
fun getTailDirection(snake: Snake): Direction {
    val tailEnd = snake.body.last()
    val beforeTailEnd = snake.body[snake.body.size - 2]
    return if (tailEnd.x == 19 && beforeTailEnd.x == 0) {
        Direction.RIGHT
    } else if (tailEnd.x == 0 && beforeTailEnd.x == 19){
        Direction.LEFT
    } else if (tailEnd.y == 14 && beforeTailEnd.y == 0){
        Direction.DOWN
    } else if (tailEnd.y == 0 && beforeTailEnd.y == 14){
        Direction.UP
    } else {
        when {
            tailEnd.x < beforeTailEnd.x -> Direction.RIGHT
            tailEnd.x > beforeTailEnd.x -> Direction.LEFT
            tailEnd.y < beforeTailEnd.y -> Direction.DOWN
            tailEnd.y > beforeTailEnd.y -> Direction.UP
            else -> snake.direction
        }
    }
}

// Define a direcao do elemento seguints para escolher a imagem certa na funcao drawSnake
// Compara as coordenadas do elemento atual da cobra e do elemento seguinte
// Define casos especificos para quando a cobra esta a passar pelos limites da arena
fun getNextSegmentDirection(snake: Snake, index: Int): Direction {
    val currentSegment = snake.body[index]
    val nextSegment = snake.body[index + 1]
    return if (currentSegment.x == 19 && nextSegment.x == 0) {
        Direction.RIGHT
    } else if (currentSegment.x == 0 && nextSegment.x == 19) {
        Direction.LEFT
    } else if (currentSegment.y == 14 && nextSegment.y == 0) {
        Direction.DOWN
    } else if (currentSegment.y == 0 && nextSegment.y == 14) {
        Direction.UP
    } else when {
        currentSegment.x < nextSegment.x -> Direction.RIGHT
        currentSegment.x > nextSegment.x -> Direction.LEFT
        currentSegment.y < nextSegment.y -> Direction.DOWN
        currentSegment.y > nextSegment.y -> Direction.UP
        else -> snake.direction
    }
}

// Define a direcao do elemento anterior para escolher a imagem certa na funcao drawSnake
// Compara as coordenadas do elemento atual da cobra e do elemento anterior
// Define casos especificos para quando a cobra esta a passar pelos limites da arena
fun getPrevSegmentDirection(snake: Snake, index: Int): Direction {
    val currentSegment = snake.body[index]
    val prevSegment = snake.body[index - 1]
    return if (currentSegment.x == 19 && prevSegment.x == 0) {
        Direction.LEFT
    } else if (currentSegment.x == 0 && prevSegment.x == 19) {
        Direction.RIGHT
    } else if (currentSegment.y == 0 && prevSegment.y == 14) {
        Direction.DOWN
    } else if (currentSegment.y == 14 && prevSegment.y == 0) {
        Direction.UP
    } else when {
        currentSegment.x < prevSegment.x -> Direction.LEFT
        currentSegment.x > prevSegment.x -> Direction.RIGHT
        currentSegment.y < prevSegment.y -> Direction.UP
        currentSegment.y > prevSegment.y -> Direction.DOWN
        else -> snake.direction
    }
}

// Faz a cobra crescer com base na variavel toGrow
fun Snake.move(): Snake {
    val newHead = nextHeadPosition()
    val newBody = if (toGrow > 0) {
        listOf(newHead) + body
    } else {
        listOf(newHead) + body.dropLast(1)
    }
    return Snake(newBody, direction, maxOf(toGrow - 1, 0))
}
// Altera a direcao da cobra. Nao permite que altere diretamente para a direcao oposta
fun Snake.changeDirection(newDirection: Direction): Snake {
    return if (newDirection != direction.opposite()) {
        Snake(body, newDirection)
    } else {
        this
    }
}
// Define a proxima posicao da cobra com base nas coordenadas de movimento
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

// Função para obter a imagem do segmento com base nas direções dos elementos anteriores e seguintes
fun getSegmentImage(prevDirection: Direction, nextDirection: Direction): String {
    if (prevDirection == nextDirection) {
        // Direcao Vertical
        return if (nextDirection in listOf(Direction.UP, Direction.DOWN)) {
            "snake|128,64,64,64"
        } else {
            "snake|64,0,64,64"  //Direcao Horizontal
        }
    } else {
        // Escolha a imagem para as curvas da cobra com base nas direcoes
        return when {
            prevDirection == Direction.UP && nextDirection == Direction.RIGHT -> "snake|0,0,64,64"
            prevDirection == Direction.RIGHT && nextDirection == Direction.UP -> "snake|128,128,64,64"
            prevDirection == Direction.DOWN && nextDirection == Direction.RIGHT -> "snake|0,64,64,64"
            prevDirection == Direction.RIGHT && nextDirection == Direction.DOWN -> "snake|128,0,64,64"
            prevDirection == Direction.UP && nextDirection == Direction.LEFT -> "snake|128,0,64,64"
            prevDirection == Direction.LEFT && nextDirection == Direction.UP -> "snake|0,64,64,64"
            prevDirection == Direction.DOWN && nextDirection == Direction.LEFT -> "snake|128,128,64,64"
            prevDirection == Direction.LEFT && nextDirection == Direction.DOWN -> "snake|0,0,64,64"
            else -> "snake|128,64,64,64"  // Escolhe um default
        }
    }
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