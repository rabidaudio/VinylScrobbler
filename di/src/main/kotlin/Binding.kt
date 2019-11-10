import androidx.annotation.CheckResult
import java.lang.ref.WeakReference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface Provider<T: Any> {
    fun get(): T
}

interface Scope {

    fun <T: Any> get(key: BindingKey<T>): T

    fun inject(receiver: Any)

    fun createChildScope(identifier: Any, vararg modules: Module): Scope
}

inline fun <reified T: Any> Scope.get(identifier: Any = Unit): T {
    return get(BindingKey(T::class, identifier))
}

data class BindingKey<T: Any>(val type: KClass<T>, val identifier: Any = Unit) {
    override fun toString(): String {
        return StringBuilder().apply {
            append("Binding")
            append('<')
            append(type.java.canonicalName)
            append('>')
            if (identifier != Unit) {
                append('(')
                append(identifier)
                append(')')
            }
        }.toString()
    }
}

interface Module {
    val name: String
    val allowOverrides: Boolean
}

internal class ModuleImpl(
    override val name: String,
    override val allowOverrides: Boolean,
    val importedModules: List<ModuleImpl>,
    val bindings: List<Binding<*>>
) : Module {

    override fun toString(): String {
        return "Module($name)"
    }
}

internal class Binding<T: Any>(
    val key: BindingKey<T>,
    val overrides: Boolean,
    val provider: Provider<T>
)

interface ScopeClosable {
    fun onScopeClose()
}

/*
- deps are keyed by a class, and an optional object (e.g. a string, default unit)
- there can not be more than one binding with the same key within a scope tree (but two
    sibling scopes could have the same binding key)
- there are no nullable bindings. that's a bit of a smell, but if you must, wrap in Optional
- generic class keys are not supported, generics are erased. If you have a need for a generic type
    binding, make an interface to use, e.g. interface IntFoo : Foo<Int>
- singleton bindings will always be scoped to the highest scope that added the module. that avoids
    weirdness around what scope a binding ends up living in based on who calls it first

TODO: overrides, set bindings, DSL, constructor injection,
 cache binding lookups,
 scope annotations, scope closable, thread-safety performance?,
 scope-creation-time tree validation and scope validation?, suspend??, eager?, callbacks?
 */

//object RootScope : Scope by ScopeImpl(RootScope, null) {
//    override fun toString(): String {
//        return "RootScope"
//    }
//}

internal class ScopeImpl(val key: Any, val parentScope: ScopeImpl?) : Scope {
    private val modules = mutableListOf<ModuleImpl>()

    private fun <T: Any> findBinding(bindingKey: BindingKey<T>): Binding<T>? {
        synchronized(DI) {
            for (module in modules) {
                for (binding in module.bindings) {
                    if (binding.key == bindingKey) return binding as Binding<T>
                }
            }
            return parentScope?.findBinding(bindingKey)
        }
    }

    private fun verifyBinding(binding: Binding<*>, allowOverride: Boolean) {
        synchronized(DI) {
            val existingBinding = findBinding(binding.key)
            if (existingBinding != null) {
                check(binding.overrides) {
                    """Duplicate binding ${binding.key} found. If you are trying to override an
                        |existing binding, set override = true on the binding (and make sure the
                        |module supports overrides)""".trimMargin()
                }
                check(allowOverride) {
                    """Binding ${binding.key} tried to override an existing binding but the module
                        |does not allow overrides.""".trimMargin()
                }
            } else {
                check(!binding.overrides) {
                    """Binding ${binding.key} marked as override but does not override an existing
                        |binding.""".trimMargin()
                }
            }
        }
    }

    private fun moduleAlreadyImported(module: ModuleImpl): Boolean {
        synchronized(DI) {
            return modules.contains(module) || (parentScope?.moduleAlreadyImported(module) ?: false)
        }
    }

    fun addModule(module: ModuleImpl) {
        synchronized(DI) {
            if (!moduleAlreadyImported(module)) {
                for (binding in module.bindings) {
                    verifyBinding(binding, module.allowOverrides)
                }
                for (importedModule in module.importedModules) {
                    addModule(importedModule)
                }
                modules.add(module)
            }
        }
    }

    override fun createChildScope(identifier: Any, vararg modules: Module): Scope {
        synchronized(DI) {
            check(!DI.scopes.contains(identifier)) { "Scope for key $identifier already exists" }
            return ScopeImpl(identifier, this).also { DI.scopes[identifier] = it }
        }
    }

    override fun <T : Any> get(key: BindingKey<T>): T {
        synchronized(DI) {
            val binding = findBinding(key)
                ?: throw IllegalStateException("$key not found in $this")
            return binding.provider.get()
        }
    }

    override fun inject(receiver: Any) {
        synchronized(DI) {
            val iterator = DI.injectedDelegateListeners.iterator()
            while (iterator.hasNext()) {
                val (ref, prop) = iterator.next()
                if (ref.get() == receiver) {
                    prop.injectValue(this)
                    iterator.remove()
                }
            }
        }
    }

    fun close() {
        synchronized(DI) {
            for (module in modules) {
                for (binding in module.bindings) {
                    if (binding.provider is SingletonProvider) binding.provider.release()
                }
            }
        }
    }

    override fun toString(): String {
        return StringBuilder().apply {
            append("Scope")
            append('<')
            append(key)
            append('>')
        }.toString()
    }
}

class JustProvider<T: Any>(private val value: T) : Provider<T> {
    override fun get(): T = value
}

class LambdaProvider<T: Any>(private val providerBlock: ProviderBlock, private val lambda: ProviderBlock.() -> T): Provider<T> {
    override fun get(): T = lambda.invoke(providerBlock)
}

class SingletonProvider<T: Any>(private val wrapped: Provider<T>): Provider<T> {
    private var value: T? = null

    fun release() {
        synchronized(this) {
            try {
                (value as? ScopeClosable)?.onScopeClose()
            } finally {
                value = null
            }
        }
    }

    override fun get(): T {
        synchronized(this) {
            return value ?: wrapped.get().also { value = it }
        }
    }
}

class BindingBlock internal constructor() {

    internal val bindings = mutableListOf<Binding<*>>()
    internal val importedModules = mutableListOf<ModuleImpl>()

    fun require(module: Module) {
        importedModules.add(module as ModuleImpl)
    }

    @CheckResult
    inline fun <reified T: Any> bind(identifier: Any = Unit, overrides: Boolean = false): PartialBindingBlock<T> {
        return PartialBindingBlock(this, BindingKey(T::class, identifier), overrides)
    }
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
        with(SingletonProvider(LambdaProvider(ProviderBlock(), block)))
    }
}

class ProviderBlock {
    fun <T: Any> get(bindingKey: BindingKey<T>): T {
        TODO("implement")
    }

    inline fun <reified T: Any> get(identifier: Any = Unit): T = get(BindingKey(T::class, identifier))
}

object DI { // TODO should this be a scope?

    object RootScope

    private val rootScope = ScopeImpl(RootScope, null)

    internal val scopes = mutableMapOf<Any, ScopeImpl>()

    internal val injectedDelegateListeners = mutableListOf<Pair<WeakReference<Any>, InjectedProperty<*>>>()

    fun addInjectedDelegate(owner: Any, property: InjectedProperty<*>) {
        synchronized(DI) {
            injectedDelegateListeners.add(Pair(WeakReference(owner), property))
        }
    }

    @CheckResult
    fun getScope(identifier: Any): Scope {
        return scopes[identifier]
            ?: throw IllegalStateException("Tried to get non-existant scope $identifier")
    }

    fun createScope(identifier: Any, vararg modules: Module) {
        rootScope.createChildScope(identifier, *modules)
    }

//
//    override fun <T : Any> get(key: BindingKey<T>): T {
//        return rootScope.get(key)
//    }
//
//    override fun inject(receiver: Any) {
//        return rootScope.inject(receiver)
//    }



//    @CheckResult
//    fun openScope(vararg scopeIdentifiers: Any): ScopeBuilder {
//        check(scopeIdentifiers.isNotEmpty()) { "Must specify at least one scope identifier" }
//        for (i in 0 until scopeIdentifiers.size - 1) {
//            val scopeIdentifier = scopeIdentifiers[i]
//            val scope = scopes[scopeIdentifier]
//                ?: throw IllegalStateException("Scope not found for key $scopeIdentifier")
////            if (i == 0 && scope == RootScope) continue
////            check(scope != RootScope) { "RootScope must only be used at the root" }
//            check((i == 0 && scope.parentScope == null)
//                    || scope.parentScope == (scopes[scopeIdentifiers[i - 1]] as ScopeImpl)) {
//                "Invalid parent scope hierarchy"
//            }
//        }
////        val parentScopeIdentifiers = listOf(RootScope) + scopeIdentifiers.dropLast(1)
//        val thisIdentifier = scopeIdentifiers.last()
//        check(!scopes.contains(thisIdentifier)) {
//            "Scope $thisIdentifier already defined"
//        }
//        val parentIdentifier = if (scopeIdentifiers.size > 1) scopeIdentifiers[scopeIdentifiers.lastIndex - 1] else null
//        val parentScope = scopes[parentIdentifier]
//        val scope = ScopeImpl(thisIdentifier, parentScope)
//        scopes[thisIdentifier] = scope
//
//        return ScopeBuilder(scope)
//    }

//    private fun scopeIdentifierTree(scopeIdentifier: Any): List<Any> {
//        val scope = scopes[scopeIdentifier] ?: return emptyList()
//        scope.parentScope?.let { listOf(scope) + scopeIdentifierTree(it) } ?: listOf(scopeIdentifier)
//    }

    fun closeScope(identifier: Any) {
//        check(identifier != RootScope) { "RootScope cannot be closed" }
        synchronized(DI) {
            val scope = scopes[identifier] ?:
                throw IllegalStateException("Tried to close non-existant scope $identifier")
            scope.close()
            scopes.remove(identifier)
        }
    }
}

class InjectedProperty<T: Any>(private val bindingKey: BindingKey<T>) {
    private lateinit var value: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    internal fun injectValue(scopeImpl: ScopeImpl) {
        value = scopeImpl.get(bindingKey)
    }
}

inline fun <reified T: Any> inject(identifier: Any = Unit): InjectedProperty<T> {
    return InjectedProperty(BindingKey(T::class, identifier))
}

fun module(name: String, allowOverrides: Boolean = false, block: BindingBlock.() -> Unit): Module {
    val bindingScope = BindingBlock()
    block.invoke(bindingScope)
    return ModuleImpl(name, allowOverrides, bindingScope.importedModules, bindingScope.bindings)
}

////// Example

class Logger {
    fun log(message: String) {
        println(message)
    }
}

class LoggerProvider : Provider<Logger> {
    override fun get(): Logger {
        return Logger()
    }
}

interface IDatabase
class Database: IDatabase

val AppModule = module("App") {
    require(LoggingModule)
    require(DataModule)

    bind<String>("AppName").with("MyApp")
}

val DataModule = module("Data") {

    require(LoggingModule)

    bind<IDatabase>().withSingleton { Database() }
}

val LoggingModule = module("Logging") {
    bind<Logger>().with(LoggerProvider())
}

class A1VM(database: IDatabase, appName: String)

val Activity1Module = module("Activity1") {

    bind<A1VM>().withSingleton { A1VM(database = get(), appName = get("AppName")) }
}

class A2VM(logger: Logger, database: IDatabase)

val Activity2Module = module("Activity2") {

    bind<A2VM>().withSingleton { A2VM(logger = get(), database = get()) }
}

class F1VM(logger: Logger, a2vm: A2VM)

val FragmentModule = module("Fragment") {

    bind<F1VM>().withSingleton { F1VM(logger = get(), a2vm = get()) }
}

class Application {

    fun onApplicationCreate() {
        DI.createScope(Application::class, AppModule)
    }
}

class Activity1 {
    val viewModel by inject<A1VM>()
    val logger by inject<Logger>()

    fun onCreate() {
        DI.getScope(Application::class)
            .createChildScope(Activity1::class, Activity1Module)
            .inject(this)
        logger.log("foo")
    }

    fun onDestroy() {
        DI.closeScope(Activity1::class)
    }
}

class Activity2 {
    val viewModel by inject<A2VM>()

    fun onCreate() {
        DI.getScope(Application::class)
            .createChildScope(Activity2::class, Activity2Module)
            .inject(this)
    }

    fun onDestroy() {
        DI.closeScope(Activity2::class)
    }
}

class Fragment {

    val aViewModel by inject<A1VM>()
    val fViewModel by inject<F1VM>()

    fun onAttach() {
        DI.getScope(Activity1::class)
            .createChildScope(Fragment::class)
            .inject(this)
    }

    fun onDetach() {
        DI.closeScope(Fragment::class)
    }
}
