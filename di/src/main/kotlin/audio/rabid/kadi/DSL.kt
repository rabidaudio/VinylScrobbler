package audio.rabid.kadi

import androidx.annotation.CheckResult


class BindingBlock internal constructor() {
    internal val bindings = mutableListOf<Binding<*>>()
    internal val importedModules = mutableSetOf<Module>()

    fun require(module: Module) {
        importedModules.add(module)
    }

    @CheckResult
    inline fun <reified T: Any> bind(
        identifier: Any = Unit,
        overrides: Boolean = false
    ): PartialBindingBlock<T> =
        PartialBindingBlock(this, BindingKey(T::class, identifier), overrides)
}

class PartialBindingBlock<T: Any>(
    private val bindingBlock: BindingBlock,
    private val bindingKey: BindingKey<T>,
    private val overrides: Boolean
) {

    fun with(provider: Provider<T>) {
        bindingBlock.bindings.add(Binding(bindingKey, overrides, provider))
    }

    inline fun with(value: T) {
        with(JustProvider(value))
    }

    inline fun with(noinline block: ProviderBlock.() -> T) {
        with(LambdaProvider(ProviderBlock(), block))
    }

    inline fun withSingleton(provider: Provider<T>) {
        with(SingletonProvider(provider))
    }

    inline fun withSingleton(noinline block: ProviderBlock.() -> T) {
        with(
            SingletonProvider(
                LambdaProvider(
                    ProviderBlock(),
                    block
                )
            )
        )
    }
}

class ProviderBlock {
    fun <T: Any> get(bindingKey: BindingKey<T>): T {
        TODO("implement")
    }

    inline fun <reified T: Any> get(identifier: Any = Unit): T =
        get(BindingKey(T::class, identifier))
}

fun module(name: String, allowOverrides: Boolean = false, block: BindingBlock.() -> Unit): Module {
    val bindingScope = BindingBlock()
    block.invoke(bindingScope)
    return Module(
        name,
        allowOverrides,
        bindingScope.importedModules,
        bindingScope.bindings
    )
}
