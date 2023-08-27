package turtoise


data class TortoiseBlock(val commands: List<TortoiseCommand>){
    val size get() = commands.size

    operator fun get(index:Int) = commands[index]
}

interface TortoiseAlgorithm {
    fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock>
    val names: List<String>
}

class TortoiseSimpleAlgorithm(
    name: String,
    commands: List<TortoiseCommand>
) : TortoiseAlgorithm {
    private val _names = listOf(name)
    override val names: List<String>
        get() = _names
    private val turtoiseCommands = listOf(TortoiseBlock(commands))

    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return turtoiseCommands
    }
}
