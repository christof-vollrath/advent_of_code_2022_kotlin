import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

val exampleDay05 = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
    """.trimIndent()

class Day05Part1: BehaviorSpec() { init {
    Given("example input") {
        val (stacks, moves) = parseStacksAndMoves(exampleDay05)
        Then("it should have parsed 3 stacks") {
            stacks.size shouldBe 3
            stacks[0][0] shouldBe "Z"
            stacks[1][2] shouldBe "D"

            stacks[0].size shouldBe 2
            stacks[0].last() shouldBe "N"
        }
        Then("it should have parsed  4 moves") {
            moves.size shouldBe 4
            moves[1].nr shouldBe 3
            moves[1].from shouldBe 1
            moves[3].to shouldBe 2
        }
        When("executing first move") {
            val firstMove = moves.take(1)
            executeCraneMoves(firstMove, stacks)
            Then("it should have executed a move of one crate") {
                stacks[0].size shouldBe 3
                stacks[0].last() shouldBe "D"
            }
        }
        When("executing remaining move") {
            val remainingMoves = moves.takeLast(moves.size - 1)
            executeCraneMoves(remainingMoves, stacks)
            Then("it should have final state from example") {
                stacks[0].size shouldBe 1
                stacks[0].last() shouldBe "C"
                stacks[1].size shouldBe 1
                stacks[1].last() shouldBe "M"
                stacks[2].size shouldBe 4
                stacks[2].last() shouldBe "Z"
                collectTopCrates(stacks).joinToString("") shouldBe "CMZ"
            }
        }
    }
    Given("exercise input") {
        val (stacks, moves) = parseStacksAndMoves(readResource("inputDay05.txt")!!)
        When("executing moves") {
            executeCraneMoves(moves, stacks)
            Then("the top stacks should have the solution") {
                collectTopCrates(stacks).joinToString("") shouldBe "FWSHSPJWM"
            }
        }
    }
} }

class Day05Part2: BehaviorSpec() { init {
    Given("example input") {
        val (stacks, moves) = parseStacksAndMoves(exampleDay05)
        When("executing moves") {
            executeCraneMoves(moves, stacks, CrateMoverType.CrateMover9001)
            Then("it should have final state from example") {
                stacks[0].size shouldBe 1
                stacks[0].last() shouldBe "M"
                stacks[1].size shouldBe 1
                stacks[1].last() shouldBe "C"
                stacks[2].size shouldBe 4
                stacks[2].last() shouldBe "D"
                collectTopCrates(stacks).joinToString("") shouldBe "MCD"
            }
        }
    }
    Given("exercise input") {
        val (stacks, moves) = parseStacksAndMoves(readResource("inputDay05.txt")!!)
        When("executing moves") {
            executeCraneMoves(moves, stacks, CrateMoverType.CrateMover9001)
            Then("the top stacks should have the solution") {
                collectTopCrates(stacks).joinToString("") shouldBe "PWPWHGFZS"
            }
        }
    }
} }

enum class CrateMoverType { CrateMover9000, CrateMover9001 }

data class CraneMove(val from: Int, val to: Int, val nr: Int)
private fun collectTopCrates(stacks: List<ArrayDeque<String>>) =
    stacks.map { it.last() }
private fun executeCraneMoves(moves: List<CraneMove>, stacks: List<ArrayDeque<String>>, crateMoverType: CrateMoverType = CrateMoverType.CrateMover9000) {
    for (move in moves) {
        val from = stacks[move.from - 1]
        val to = stacks[move.to - 1]
        when (crateMoverType) {
            CrateMoverType.CrateMover9000 ->
                for (i in 0 until move.nr) {
                    val crateToMove = from.removeLast()
                    to.addLast(crateToMove)
                }
            CrateMoverType.CrateMover9001 -> {
                val cratesToMove = sequence {
                    for (i in 0 until move.nr)
                        yield(from.removeLast())
                }
                to.addAll(cratesToMove.toList().reversed())
            }
        }
    }
}

private fun parseStacksAndMoves(input: String): Pair<List<ArrayDeque<String>>, List<CraneMove>> {
    fun checkStackLine(line: String) = line.trim().startsWith("[")
    fun checkMoveLine(line: String) = line.trim().startsWith("move")
    fun parseStacks(lines: List<String>): List<ArrayDeque<String>> {
        val maxLength = lines.maxOf { it.trim().length }
        val nrStacks = (maxLength + 1) / 4 // each stack has 4 chars except the last
        val reversedLines = lines.reversed()
        val stacks = List(nrStacks) { ArrayDeque<String>() }
        for (line in reversedLines) {
            for (i in 0 until nrStacks) {
                val crate = line.getOrNull(i * 4 + 1)
                if (crate != null && crate != ' ')
                    stacks[i].add(crate.toString())
            }
        }
        return stacks
    }
    fun parseMoves(lines: List<String>): List<CraneMove> =
        lines.map { line ->
            val regex = """move (\d+) from (\d+) to (\d+)""".toRegex()
            val match = regex.find(line.trim()) ?: throw IllegalArgumentException("Can not parse input=$line")
            if (match.groupValues.size != 4) throw IllegalArgumentException("Wrong number of elements parsed")
            val nr = match.groupValues[1].toInt()
            val from = match.groupValues[2].toInt()
            val to = match.groupValues[3].toInt()
            CraneMove(from, to, nr)
        }

    val lines = input.split("\n")
    val stackLines = lines.filter { checkStackLine(it) }
    val stacks = parseStacks(stackLines)
    val moveLines = lines.filter { checkMoveLine(it) }
    val moves = parseMoves(moveLines)
    return Pair(stacks, moves)
}

