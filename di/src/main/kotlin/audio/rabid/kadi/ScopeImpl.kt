package audio.rabid.kadi

internal class ScopeImpl internal constructor(
    private val key: Any,
    private val parentScope: ScopeImpl?
) : ChildScope {
    private val modules = mutableListOf<Module>()

    private fun <T: Any> findBinding(bindingKey: BindingKey<T>): Binding<T>? {
        synchronized(Kadi) {
            for (module in modules) {
                for (binding in module.bindings) {
                    @Suppress("UNCHECKED_CAST")
                    if (binding.key == bindingKey) return binding as Binding<T>
                }
            }
            return parentScope?.findBinding(bindingKey)
        }
    }

    private fun verifyBinding(binding: Binding<*>, allowOverride: Boolean) {
        synchronized(Kadi) {
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

    private fun moduleAlreadyImported(module: Module): Boolean {
        synchronized(Kadi) {
            return modules.contains(module) || (parentScope?.moduleAlreadyImported(module) ?: false)
        }
    }

    fun addModule(module: Module) {
        synchronized(Kadi) {
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

    override fun createChildScope(identifier: Any, vararg modules: Module): ChildScope {
        synchronized(Kadi) {
            check(!Kadi.scopes.contains(identifier)) { "Scope for key $identifier already exists" }
            return ScopeImpl(identifier, this).apply {
                for (module in modules) {
                    addModule(module)
                }
            }.also { Kadi.scopes[identifier] = it }
        }
    }

    override fun <T : Any> get(key: BindingKey<T>): T {
        synchronized(Kadi) {
            val binding = findBinding(key)
                ?: throw IllegalStateException("$key not found in $this")
            return binding.provider.get()
        }
    }

    override fun inject(receiver: Any) {
        InjectedProperty.inject(this, receiver)
    }

    override fun close() {
        synchronized(Kadi) {
            for (module in modules) {
                for (binding in module.bindings) {
                    if (binding.provider is SingletonProvider) binding.provider.release()
                }
            }
            Kadi.scopes.remove(key)
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
