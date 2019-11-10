package audio.rabid.kadi

internal class Binding<T: Any>(
    val key: BindingKey<T>,
    val overrides: Boolean,
    val provider: Provider<T>
)
