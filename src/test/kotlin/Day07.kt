import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

val exampleInputDay07 = """
    # cd /
    # ls
    dir a
    14848514 b.txt
    8504156 c.dat
    dir d
    # cd a
    # ls
    dir e
    29116 f
    2557 g
    62596 h.lst
    # cd e
    # ls
    584 i
    # cd ..
    # cd ..
    # cd d
    # ls
    4060174 j
    8033020 d.log
    5626152 d.ext
    7214296 k
    """.trimIndent().replace('#', '$') // string constant contains # because $ has special meaning in kotlin strings

class Day07Part1: BehaviorSpec() { init {
    Given("command input") {
        When("parsing input") {
            val commands = parseFileSystemCommands(exampleInputDay07)
            Then("it should have parsed 10 commands") {
                commands.size shouldBe 10
            }
            Then("it should have parsed cd command correctly") {
                val command3 = commands[2]
                command3.shouldBeInstanceOf<CdSystemCommand>()
                command3.dir shouldBe "a"
            }
            Then("it should have parsed ls command correctly") {
                val command2 = commands[1]
                command2.shouldBeInstanceOf<LsSystemCommand>()
                command2.files.size shouldBe 4
                val fileData4 = command2.files[3]
                fileData4.shouldBeInstanceOf<DirFileData>()
                fileData4.name shouldBe "d"
                val fileData2 = command2.files[1]
                fileData2.shouldBeInstanceOf<PlainFileData>()
                fileData2.name shouldBe "b.txt"
                fileData2.size shouldBe 14848514
            }
            When("executing commands") {
                val fileSystem = executeFileSystemCommands(commands)
                Then("file system root should contain 4 files") {
                    fileSystem.root.files.size shouldBe 4
                }
                Then("should have created deep file") {
                    val dirA = fileSystem.root.files["a"]
                    dirA.shouldBeInstanceOf<FileSystemDirectory>()
                    val dirE = dirA.files["e"]
                    dirE.shouldBeInstanceOf<FileSystemDirectory>()
                    val fileI = dirE.files["i"]
                    fileI.shouldBeInstanceOf<FileSystemPlainFile>()
                    fileI.name shouldBe "i"
                    fileI.size shouldBe 584
                }
                When("calculating total sizes") {
                    fileSystem.calculateTotalSizes()
                    Then("should have calculated the total sizes") {
                        fileSystem.root.totalSize shouldBe 48381165
                        fileSystem.root.dirs["a"]?.dirs?.get("e")?.totalSize shouldBe 584
                    }
                    When("listing all dir sizes") {
                        val dirs = fileSystem.allDirsBySize().reversed()
                        Then("result should be sorted") {
                            dirs[0].totalSize shouldBe 584
                            dirs[1].totalSize shouldBe 94853
                        }
                        Then("sum of dirs with total size <= 100000") {
                            dirs.filter { it.totalSize < 100000 }.sumOf { it.totalSize } shouldBe 95437
                        }
                    }
                }
            }
        }
    }
    Given("exercise input") {
        val execiseInput = readResource("inputDay07.txt")!!
        When("parsed, executed and total size calculated") {
            val fileSystem = executeFileSystemCommands(parseFileSystemCommands(execiseInput))
            fileSystem.calculateTotalSizes()
            Then("total size should be right") {
                fileSystem.root.totalSize shouldBe 42080344
            }
            Then("sum of dirs with total size <= 100000") {
                val dirs = fileSystem.allDirsBySize()
                dirs.filter { it.totalSize < 100000 }.sumOf { it.totalSize } shouldBe 1611443
            }
        }
    }
} }

class Day07Part2: BehaviorSpec() { init {
    Given("file system dirs") {
        val commands = parseFileSystemCommands(exampleInputDay07)
        val fileSystem = executeFileSystemCommands(commands)
        fileSystem.calculateTotalSizes()
        When("finding dir to delete") {
            val dirSize = findDirToDelete(70000000, 30000000, fileSystem)
            Then("it should have found the best dir") {
                dirSize shouldBe 24933642
            }
        }
    }
    Given("exercise input") {
        val execiseInput = readResource("inputDay07.txt")!!
        val commands = parseFileSystemCommands(execiseInput)
        val fileSystem = executeFileSystemCommands(commands)
        fileSystem.calculateTotalSizes()
        When("finding dir to delete") {
            val dirSize = findDirToDelete(70000000, 30000000, fileSystem)
            Then("it should have found the best dir") {
                dirSize shouldBe 2086088
            }
        }
    }
} }

private fun findDirToDelete(totalDiskSize: Int, needed: Int, fileSystem: FileSystem): Int {
    val dirs = fileSystem.allDirsBySize()
    val maxSizeUsed = totalDiskSize - needed
    val sizeToBeFreed = fileSystem.root.totalSize - maxSizeUsed
    val dirsBigEnough = dirs.filter { it.totalSize >= sizeToBeFreed }.map { it.totalSize}
    return dirsBigEnough.min()
}

private fun executeFileSystemCommands(commands: List<FileSystemCommand>): FileSystem {
    val root = FileSystemDirectory("/")
    var currentDir: FileSystemDirectory? = null
    for (command in commands) {
        when(command) {
            is CdSystemCommand ->
                currentDir = when(command.dir) {
                    "/" -> root
                    ".." -> currentDir?.parent ?: throw IllegalArgumentException("dir $currentDir has no parent dir")
                    else -> {
                        val moveTo = currentDir?.files?.get(command.dir) ?: throw IllegalArgumentException("dir $currentDir has no sub diriectory ${command.dir}")
                        if (moveTo !is FileSystemDirectory) throw IllegalArgumentException("file ${command.dir} in dir $currentDir is no directory")
                        moveTo
                    }
                }
            is LsSystemCommand -> currentDir?.files = command.files.associate { fileData ->
                when (fileData) {
                    is PlainFileData -> Pair(fileData.name, FileSystemPlainFile(fileData.name, fileData.size))
                    is DirFileData -> Pair(fileData.name, FileSystemDirectory(fileData.name, currentDir))
                }
            }
        }
    }
    return FileSystem(root)
}

private fun parseFileSystemCommands(input: String): List<FileSystemCommand> {
    val lines = input.split("\n").map { it.trim() }
    return sequence {
        var parsingLsOutput = false
        var parsedFileData = mutableListOf<FileData>()
        for (line in lines) {
            val parts = line.split(" ")
            if (parts[0] == "$") {
                when (parts[1]) {
                    "cd" -> {
                        if (parsingLsOutput) {
                            yield(LsSystemCommand(parsedFileData))
                            parsedFileData = mutableListOf()
                            parsingLsOutput = false
                        }
                        yield(CdSystemCommand(parts[2]))
                    }

                    "ls" -> parsingLsOutput = true
                }
            } else {
                val fileData = if (parts[0] == "dir")
                    DirFileData(parts[1])
                else PlainFileData(parts[1], parts[0].toInt())
                parsedFileData.add(fileData)
            }
        }
        if (parsingLsOutput) {
            yield(LsSystemCommand(parsedFileData))
        }
    }.toList()
}

interface FileSystemCommand
data class CdSystemCommand(val dir: String) : FileSystemCommand
data class LsSystemCommand(val files: List<FileData>) : FileSystemCommand

sealed class FileData { abstract val name: String }
data class PlainFileData(override val name: String, val size: Int) : FileData()
data class DirFileData(override val name: String) : FileData()

data class FileSystem(val root: FileSystemDirectory) {
    fun calculateTotalSizes() = root.calculateTotalSizes()
    fun allDirsBySize() = root.allSubDirs().sortedByDescending { it.totalSize }
}

sealed class FileSystemFile {
    abstract val name: String }
data class FileSystemDirectory(
    override val name: String,
    val parent: FileSystemDirectory? = null,
    var files: Map<String, FileSystemFile> = emptyMap(),
    var totalSize: Int = 0) : FileSystemFile() {
        val dirs: Map<String, FileSystemDirectory>
            get() = files.entries.filter { (k, v) -> v is FileSystemDirectory }.associate { (k, v) -> Pair(k, v) } as Map<String, FileSystemDirectory>

        fun calculateTotalSizes() {
            for (dir in dirs.values) dir.calculateTotalSizes()
            totalSize = files.values.sumOf {
                when(it) {
                    is FileSystemDirectory -> it.totalSize
                    is FileSystemPlainFile -> it.size
                } }
        }
        fun allSubDirs(): List<FileSystemDirectory> = dirs.values + dirs.values.flatMap { it.allSubDirs() }
}
data class FileSystemPlainFile(override val name: String, val size: Int) : FileSystemFile()