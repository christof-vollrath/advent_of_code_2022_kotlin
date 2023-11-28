import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

val exampleInputDay10 = """
    addx 15
    addx -11
    addx 6
    addx -3
    addx 5
    addx -1
    addx -8
    addx 13
    addx 4
    noop
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx -35
    addx 1
    addx 24
    addx -19
    addx 1
    addx 16
    addx -11
    noop
    noop
    addx 21
    addx -15
    noop
    noop
    addx -3
    addx 9
    addx 1
    addx -3
    addx 8
    addx 1
    addx 5
    noop
    noop
    noop
    noop
    noop
    addx -36
    noop
    addx 1
    addx 7
    noop
    noop
    noop
    addx 2
    addx 6
    noop
    noop
    noop
    noop
    noop
    addx 1
    noop
    noop
    addx 7
    addx 1
    noop
    addx -13
    addx 13
    addx 7
    noop
    addx 1
    addx -33
    noop
    noop
    noop
    addx 2
    noop
    noop
    noop
    addx 8
    noop
    addx -1
    addx 2
    addx 1
    noop
    addx 17
    addx -9
    addx 1
    addx 1
    addx -3
    addx 11
    noop
    noop
    addx 1
    noop
    addx 1
    noop
    noop
    addx -13
    addx -19
    addx 1
    addx 3
    addx 26
    addx -30
    addx 12
    addx -1
    addx 3
    addx 1
    noop
    noop
    noop
    addx -9
    addx 18
    addx 1
    addx 2
    noop
    noop
    addx 9
    noop
    noop
    noop
    addx -1
    addx 2
    addx -37
    addx 1
    addx 3
    noop
    addx 15
    addx -21
    addx 22
    addx -6
    addx 1
    noop
    addx 2
    addx 1
    noop
    addx -10
    noop
    noop
    addx 20
    addx 1
    addx 2
    addx 2
    addx -6
    addx -11
    noop
    noop
    noop
    """.trimIndent()
class Day10Part1: BehaviorSpec() { init {
    Given("example instructions input") {
        When("parsing input") {
            val instructions = parseInstructions(exampleInputDay10)
            Then("it should have parsed 146 instructions") {
                instructions.size shouldBe 146
            }
            Then("it should have parsed right instructions") {
                instructions[0] shouldBe AddxInstruction(15)
                instructions[143].shouldBeInstanceOf<NoopInstruction>()
            }
            When("executing programm") {
                val cpu = Cpu()
                cpu.executeProgram(instructions)
                Then("trace should not be empty") {
                    cpu.trace.size shouldBeGreaterThan 0
                }
                Then("trace should contain correct signal strength") {
                    cpu.trace[19] shouldBe CpuTraceData(20, 420)
                    cpu.trace[59] shouldBe CpuTraceData(60, 1140)
                    cpu.trace[179] shouldBe CpuTraceData(180, 2880)
                    cpu.trace[219] shouldBe CpuTraceData(220, 3960)
                }
                Then("correct cycle sum should be calculated") {
                    cycleSum(cpu.trace) shouldBe 13140
                }
            }
        }
    }
    Given("A small program") {
        val smallProgramStr = """
            noop
            addx 3
            addx -5
        """.trimIndent()
        val smallProgram = parseInstructions(smallProgramStr)
        When("executing the program") {
            val cpu = Cpu()
            cpu.executeProgram(smallProgram)
            Then("register x should have changed") {
                cpu.x shouldBe -1
            }
            Then("the trace should be correct") {
                cpu.trace shouldBe listOf(
                    CpuTraceData(1, 1),
                    CpuTraceData(2, 2),
                    CpuTraceData(3, 3),
                    CpuTraceData(4, 16),
                    CpuTraceData(5, 20),
                )
            }
        }
    }
    Given("exercise input") {
        val exerciseInput = readResource("inputDay10.txt")!!
        When("parsing input and executing instructions") {
            val program = parseInstructions(exerciseInput)
            val cpu = Cpu()
            cpu.executeProgram(program)
            Then("correct cycle sum should be calculated") {
                cycleSum(cpu.trace) shouldBe 15880
            }
        }
    }
} }

class Day10Part2: BehaviorSpec() { init {
    Given("example instructions input") {
        val instructions = parseInstructions(exampleInputDay10)
        When("connecting a crt and a cpu") {
            val crt = Crt()
            val cpu = Cpu(crt)
            Then("the crt should start empty at pos 0,0 ") {
                crt[0].forEach { pixel ->
                    pixel shouldBe '.'
                }
                crt.pos shouldBe Coord2(0, 0)
            }
            When("having executed one instruction") {
                cpu.executeProgram(instructions, 1)
                Then("the crt pos should have moved to the right") {
                    crt.pos shouldBe Coord2(2, 0)
                }
                Then("the crt should have the first pixel set") {
                    crt[0][0] shouldBe '#'
                    crt[0][1] shouldBe '#'
                }
            }
            When("having executed another instruction") {
                cpu.executeProgram(instructions, 1)
                Then("the crt pos should have moved to the right") {
                    crt.pos shouldBe Coord2(4, 0)
                }
            }
            When("having executed all instructions") {
                cpu.executeProgram(instructions)
                Then("the crt first line should look like this") {
                    crt[0].joinToString("") shouldBe "##..##..##..##..##..##..##..##..##..##.."
                    crt[1].joinToString("") shouldBe "###...###...###...###...###...###...###."
                    crt[2].joinToString("") shouldBe "####....####....####....####....####...."
                    crt[3].joinToString("") shouldBe "#####.....#####.....#####.....#####....."
                    crt[4].joinToString("") shouldBe "######......######......######......####"
                    crt[5].joinToString("") shouldBe "#######.......#######.......#######....."
                }
            }
        }
    }
    Given("exercise input") {
        val exerciseInput = readResource("inputDay10.txt")!!
        When("parsing input and executing instructions") {
            val program = parseInstructions(exerciseInput)
            val crt = Crt()
            val cpu = Cpu(crt)
            cpu.executeProgram(program)
            Then("display the crt") {
                println(crt.toString())
            }
        }
    }

} }

private fun parseInstructions(input: String) = input.split("\n").map { line ->
    val (instr, i: String?) = line.split(" ").map { it.trim() } + null
    when (instr) {
        "addx" -> AddxInstruction(i?.toInt() ?: 0)
        "noop" -> NoopInstruction()
        else -> throw IllegalArgumentException("unexpected instr $instr")
    }
}

sealed class CpuInstruction()
data class AddxInstruction(val p: Int) : CpuInstruction()
class NoopInstruction() : CpuInstruction()
class Cpu(val crt: Crt? = null, var x: Int = 1, var cycle: Int = 1, var pc: Int = 0) {
    fun executeProgram(program: List<CpuInstruction>, nrInstrToExecute: Int = 0) {
        fun incrCycle() {
            cycle++
            crt?.drawAndMove(x) // Crt pos follows cycle
        }
        var currentNrExecuted = 0
        while(pc < program.size) {
            if (nrInstrToExecute > 0) { // only execute some steps
                if (currentNrExecuted >= nrInstrToExecute) break
            }
            val instr = program[pc]
            when(instr) {
                is AddxInstruction -> {
                    _trace.add(CpuTraceData(cycle, signalStrength))
                    incrCycle()
                    _trace.add(CpuTraceData(cycle, signalStrength))
                    incrCycle()
                    x += instr.p
                }
                is NoopInstruction -> {
                    _trace.add(CpuTraceData(cycle, signalStrength))
                    incrCycle()
                }
            }
            pc++
            currentNrExecuted++
        }
    }

    val signalStrength: Int
        get() = x * cycle

    val _trace = mutableListOf<CpuTraceData>()
    val trace: List<CpuTraceData>
        get() = _trace
}

data class Crt(val rows: List<MutableList<Char>> = List(6, { MutableList(40, { '.' }) }), var x: Int = 0, var y: Int = 0) {
    val pos: Coord2
        get() = Coord2(x, y)

    operator fun get(i: Int) = rows[i]
    fun drawAndMove(spritePos: Int) {
        if (x >= spritePos-1 && x <= spritePos+1)
            rows[y][x] = '#'
        x++
        if (x >= 40) { // go to next line
            x = 0
            y++
        }
        if (y >= 6) { // start again from top
            y = 0
        }
    }

    override fun toString(): String = rows.map { line -> line.joinToString("") }.joinToString("\n")
}

data class CpuTraceData(val cycle: Int, val signalStrength: Int)

private fun cycleSum(trace: List<CpuTraceData>) =
    sequence {
        for (i in 20 .. 220 step 40) {
            yield(trace[i-1].signalStrength)
        }
    }.sum()
