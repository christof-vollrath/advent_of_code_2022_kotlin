import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

val exampleSectionsString = """
    2-4,6-8
    2-3,4-5
    5-7,7-9
    2-8,3-7
    6-6,4-6
    2-6,4-8
    """.trimIndent()

class Day04Part1: BehaviorSpec() { init {
    Given("example input") {
        val exampleSections = parseSections(exampleSectionsString)
        Then("it should have found 6 sections") {
            exampleSections.size shouldBe 6
            exampleSections[0] shouldBe Pair(Pair(2, 4), Pair(6, 8))
        }
        Then("find fully containing section pairs") {
            fullyContaining(exampleSections[0]) shouldBe false
            fullyContaining(exampleSections[3]) shouldBe true
            fullyContaining(exampleSections[4]) shouldBe true
        }
        Then("count containing section pairs") {
            countFullyContaining(exampleSections) shouldBe 2
        }
    }
    Given("exercise input") {
        val exerciseSections = parseSections(readResource("inputDay04.txt")!!)
        Then("count containing section pairs") {
            countFullyContaining(exerciseSections) shouldBe 532
        }
    }
} }
class Day04Part2: BehaviorSpec() { init {
    Given("example input") {
        val exampleSections = parseSections(exampleSectionsString)
        Then("find overlapping section pairs") {
            overlapping(exampleSections[0]) shouldBe false
            overlapping(exampleSections[2]) shouldBe true
            overlapping(exampleSections[3]) shouldBe true
            overlapping(exampleSections[4]) shouldBe true
            overlapping(exampleSections[5]) shouldBe true
        }
        Then("count overlapping section pairs") {
            countOverlapping(exampleSections) shouldBe 4
        }
    }
    Given("exercise input") {
        val exerciseSections = parseSections(readResource("inputDay04.txt")!!)
        Then("count overlapping section pairs") {
            countOverlapping(exerciseSections) shouldBe 854
        }
    }
} }

private fun countFullyContaining(sectionsList: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>) = sectionsList.count { fullyContaining(it)}

private fun fullyContaining(sections: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean =
    firstFullyContainingSecond(sections) || firstFullyContainingSecond(Pair(sections.second, sections.first)) // or reverted
private fun firstFullyContainingSecond(sections: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean =
    sections.second.first >= sections.first.first && sections.second.second <= sections.first.second

private fun countOverlapping(sectionsList: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>) = sectionsList.count { overlapping(it)}

private fun overlapping(sections: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean =
    firstOverlappingSecond(sections) || firstOverlappingSecond(Pair(sections.second, sections.first)) // or reverted
private fun firstOverlappingSecond(sections: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean =
    sections.second.first <= sections.first.second && sections.second.second >= sections.first.first

private fun parseSections(input: String) =
    input.split("\n").map { line ->
        val sections = line.split(",").map {parts ->
            val section = parts.trim().split("-").map { it.trim().toInt() }
            Pair(section[0], section[1])
        }
        Pair(sections[0], sections[1])
    }
