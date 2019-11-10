package audio.rabid.kadi

class Module internal constructor(
    val name: String,
    val allowOverrides: Boolean,
    internal val importedModules: Set<Module>,
    internal val bindings: List<Binding<*>>
) {

    override fun toString(): String {
        return "Module($name)"
    }
}
