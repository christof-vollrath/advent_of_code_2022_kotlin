import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

val exampleInputDay08 = """
    30373
    25512
    65332
    33549
    35390
    """.trimIndent()

class Day08Part1: BehaviorSpec() { init {
    Given("tree map input") {
        When("parsing input") {
            val treeMap = parseTreeMap(exampleInputDay08)
            Then("it should have parsed the map to a 5x5 map") {
                treeMap.size shouldBe 5
                for (line in treeMap) line.size shouldBe 5
            }
            Then("the map should contain the right tree heights") {
                treeMap[0][0] shouldBe 3
                treeMap[0][3] shouldBe 7
                treeMap[2][1] shouldBe 5
            }
            When("searching for the highest tree") {
                Then("it should find the right trees") {
                    heighestTree(treeMap, Coord2(0, 1), Coord2(1, 0)) shouldBe Tree(Coord2(1, 1), 5)
                    heighestTree(treeMap, Coord2(1, 0), Coord2(0, 1)) shouldBe Tree(Coord2(1, 1), 5)
                    heighestTree(treeMap, Coord2(4, 1), Coord2(-1, 0)) shouldBe Tree(Coord2(2, 1), 5)
                    heighestTree(treeMap, Coord2(1, 4), Coord2(0, -1)) shouldBe Tree(Coord2(1, 4), 5)
                }
            }
            When("searching for visible trees") {
                Then("it should find the right trees") {
                    visibleTrees(treeMap, Coord2(0, 1), Coord2(1, 0)) shouldBe listOf(
                        Tree(Coord2(0, 1), 2),
                        Tree(Coord2(1, 1), 5)
                    )
                    visibleTrees(treeMap, Coord2(1, 0), Coord2(0, 1)) shouldBe listOf(
                        Tree(Coord2(1, 0), 0),
                        Tree(Coord2(1, 1), 5)
                    )
                    visibleTrees(treeMap, Coord2(4, 1), Coord2(-1, 0)) shouldBe listOf(
                        Tree(Coord2(4, 1), 2),
                        Tree(Coord2(2, 1), 5)
                    )
                    visibleTrees(treeMap, Coord2(1, 4), Coord2(0, -1)) shouldBe listOf(Tree(Coord2(1, 4), 5))
                }
            }
            When("searching for all visible trees") {
                Then("we should find 21") {
                    allVisibleTrees(treeMap).size shouldBe 21
                }
            }
        }
    }

    Given("exercise input") {
        val execiseInput = readResource("inputDay08.txt")!!
        When("parse tree map") {
            val treeMap = parseTreeMap(execiseInput)
            Then("it should have parsed the map to a 99x99 map") {
                treeMap.size shouldBe 99
                for (line in treeMap) line.size shouldBe 99
            }
            When("searching for all visible trees") {
                Then("we should find the solution") {
                    allVisibleTrees(treeMap).size shouldBe 1647
                }
            }
        }
    }
} }

class Day08Part2: BehaviorSpec() { init {
    Given("tree map") {
        val treeMap = parseTreeMap(exampleInputDay08)
        Then("it should find visible trees from the tree house") {
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 1), Coord2(0, -1)) shouldBe listOf(
                Tree(Coord2(2, 0), 3)
            )
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 1), Coord2(-1, 0)) shouldBe listOf(
                Tree(Coord2(1, 1), 5)
            )
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 1), Coord2(0, 1)) shouldBe listOf(
                Tree(Coord2(2, 2), 3),
                Tree(Coord2(2, 3), 5)
            )
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 3), Coord2(0, -1)).size shouldBe 2
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 3), Coord2(-1, 0)).size shouldBe 2
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 3), Coord2(0, 1)).size shouldBe 1
            visibleTreesFromTreeHouse(treeMap, Coord2(2, 3), Coord2(1, 0)).size shouldBe 2
        }
        Then("it should calculate the scenic score") {
            scenicScore(treeMap, Coord2(2, 1)) shouldBe 4
            scenicScore(treeMap, Coord2(2, 3)) shouldBe 8
        }
        Then("it should find the best scenic score") {
            bestScenicScore(treeMap) shouldBe 8
        }
    }

    Given("exercise input") {
        val execiseInput = readResource("inputDay08.txt")!!
        val treeMap = parseTreeMap(execiseInput)
        Then("it should find the best scenic score") {
            bestScenicScore(treeMap) shouldBe 392080
        }
    }
} }


private fun allVisibleTrees(treeMap: List<List<Int>>): Set<Tree>  = sequence {
    for(x in treeMap.indices) yield(visibleTrees(treeMap, Coord2(x, 0), Coord2(0, 1)))
    for(x in treeMap.indices) yield(visibleTrees(treeMap, Coord2(x, treeMap[x].size - 1), Coord2(0, -1)))
    for(y in treeMap[0].indices) yield(visibleTrees(treeMap, Coord2(0, y), Coord2(1, 0)))
    for(y in treeMap[0].indices) yield(visibleTrees(treeMap, Coord2(treeMap.size-1, y), Coord2(-1, 0)))
}.flatten().toSet()

private fun heighestTree(treeMap: Plane<Int>, start: Coord2, incr: Coord2): Tree {
    var result: Tree? = null
    var currentCoord = start
    while(currentCoord.x >= 0 && currentCoord.y >= 0
        && currentCoord.x < treeMap.size && currentCoord.y < treeMap[currentCoord.x].size) {
        if (result == null || result.height < treeMap[currentCoord]) result = Tree(currentCoord, treeMap[currentCoord])
        currentCoord += incr
    }
    return result!! // can not be null since at least one tree will be found
}
private fun visibleTrees(treeMap: List<List<Int>>, start: Coord2, incr: Coord2): Set<Tree> =
    sequence {
        var currentCoord = start
        var highest = -1
        while(currentCoord.x >= 0 && currentCoord.y >= 0
            && currentCoord.x < treeMap.size && currentCoord.y < treeMap[currentCoord.x].size) {
            if (treeMap[currentCoord] > highest) {
                highest = treeMap[currentCoord]
                yield(Tree(currentCoord, highest))
            }
            currentCoord += incr
        }
    }.toSet()
private fun visibleTreesFromTreeHouse(treeMap: List<List<Int>>, start: Coord2, incr: Coord2): Set<Tree> =
    sequence {
        var currentCoord = start + incr
        while(currentCoord.x >= 0 && currentCoord.y >= 0
            && currentCoord.x < treeMap.size && currentCoord.y < treeMap[currentCoord.x].size) {
            yield(Tree(currentCoord, treeMap[currentCoord]))
            if (treeMap[currentCoord] >= treeMap[start]) break
            currentCoord += incr
        }
    }.toSet()

private fun scenicScore(treeMap: List<List<Int>>, treeCoord: Coord2): Int =
    visibleTreesFromTreeHouse(treeMap, treeCoord, Coord2(-1, 0)).size *
    visibleTreesFromTreeHouse(treeMap, treeCoord, Coord2(0, 1)).size *
    visibleTreesFromTreeHouse(treeMap, treeCoord, Coord2(1, 0)).size *
    visibleTreesFromTreeHouse(treeMap, treeCoord, Coord2(0, -1)).size

private fun bestScenicScore(treeMap: List<List<Int>>) = sequence {
    for (y in treeMap.indices)
        for (x in treeMap[y].indices)
            yield(scenicScore(treeMap, Coord2(x, y)))
}.max()

private fun parseTreeMap(input: String): Plane<Int> = input.split("\n").map { line ->
    line.trim().toCharArray().map { c ->
        c.toString().toInt()
    }
}
data class Tree(val coord: Coord2, val height: Int)
