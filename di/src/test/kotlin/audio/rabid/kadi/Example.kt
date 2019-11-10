package audio.rabid.kadi

import kotlin.reflect.KClass

object Example {

    class Logger {
        fun log(message: String) {
            println(message)
        }
    }

    class LoggerProvider : Provider<Logger> {
        override fun get(): Logger = Logger()
    }

    val LoggingModule = module("Logging") {
        bind<Logger>().with(LoggerProvider())
    }

    interface IDatabase
    class Database: IDatabase, ScopeClosable {

        var isClosed = false

        override fun onScopeClose() {
            isClosed = true
        }
    }

    val DataModule = module("Data") {
        require(LoggingModule)
        bind<IDatabase>().withSingleton { Database() }
    }

    class Application {
        fun onApplicationCreate() {
            Kadi.createChildScope(Application::class, AppModule)
        }
    }

    val AppModule = module("App") {
        require(LoggingModule)
        require(DataModule)
        bind<String>("AppName").with("MyApp")
    }


    class Activity1 {
        val viewModel by inject<Activity1ViewModel>()
        val logger by inject<Logger>()

        fun onCreate() {
            Kadi.getScope(Application::class)
                .createChildScope(Activity1::class, Activity1Module)
                .inject(this)
            logger.log("foo")
        }

        fun onDestroy() {
            Kadi.closeScope(Activity1::class)
        }
    }

    class Activity1ViewModel(val database: IDatabase, val appName: String)

    val Activity1Module = module("Activity1") {

        bind<Activity1ViewModel>().withSingleton {
            Activity1ViewModel(
                database = get(),
                appName = get("AppName")
            )
        }
    }

    class Fragment {
        val viewModel by inject<FragmentViewModel>()

        fun onAttach(parentScope: KClass<*>) {
            Kadi.getScope(parentScope)
                .createChildScope(Fragment::class, FragmentModule)
                .inject(this)
        }

        fun onDetach() {
            Kadi.closeScope(Fragment::class)
        }
    }

    class FragmentViewModel(val logger: Logger)

    val FragmentModule = module("Fragment") {

        bind<FragmentViewModel>().withSingleton {
            FragmentViewModel(logger = get())
        }
    }

    class Activity2ViewModel(val logger: Logger, val database: IDatabase)

    val Activity2Module = module("Activity2") {

        bind<Activity2ViewModel>().withSingleton {
            Activity2ViewModel(
                logger = get(),
                database = get()
            )
        }
    }

    class Activity2 {
        val viewModel by inject<Activity2ViewModel>()

        fun onCreate() {
            Kadi.getScope(Application::class)
                .createChildScope(Activity2::class, Activity2Module)
                .inject(this)
        }

        fun onDestroy() {
            Kadi.closeScope(Activity2::class)
        }
    }
}
