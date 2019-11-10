package audio.rabid.kadi

import audio.rabid.kadi.Example.Activity1
import audio.rabid.kadi.Example.Activity1ViewModel
import audio.rabid.kadi.Example.Activity2
import audio.rabid.kadi.Example.Activity2ViewModel
import audio.rabid.kadi.Example.Application
import audio.rabid.kadi.Example.Database
import audio.rabid.kadi.Example.Fragment
import audio.rabid.kadi.Example.FragmentViewModel
import audio.rabid.kadi.Example.IDatabase
import audio.rabid.kadi.Example.Logger
import com.winterbe.expekt.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IntegrationSpec : Spek({

    defaultTimeout = 600_000

    describe("Dummy Application") {

        val application by memoized { Application() }
        val activity1 by memoized { Activity1() }
        val fragment1 by memoized { Fragment() }
        val activity2 by memoized { Activity2() }
        val fragment2 by memoized { Fragment() }

        it("should allow the creation of child scopes") {

            application.onApplicationCreate()
            val appScope = Kadi.getScope(Application::class)
            expect(appScope.get<Logger>()).to.be.an.instanceof(Logger::class.java)
            expect(appScope.get<String>("AppName")).to.equal("MyApp")
            expectToThrow<IllegalStateException> { appScope.get<Activity1ViewModel>() }
            expectToThrow<IllegalStateException> { Kadi.get<Logger>() }

            activity1.onCreate()

            expect(activity1.logger).to.be.an.instanceof(Logger::class.java)
            expect(activity1.viewModel).to.be.an.instanceof(Activity1ViewModel::class.java)
            expect(activity1.viewModel.appName).to.equal("MyApp")
            expect(activity1.viewModel.database).to.be.an.instanceof(Database::class.java)
            expect(activity1.viewModel.database).to.satisfy { it === appScope.get<IDatabase>() }

            fragment1.onAttach(Activity1::class)
            expect(fragment1.viewModel).to.be.an.instanceof(FragmentViewModel::class.java)
            expect(Kadi.getScope(Fragment::class).get<Activity1ViewModel>()).to.satisfy { it === activity1.viewModel }
            expectToThrow<IllegalStateException> {
                Kadi.getScope(Activity1::class).get<FragmentViewModel>()
            }
            expect(Kadi.getScope(Fragment::class).get<FragmentViewModel>()).to.equal(fragment1.viewModel)

            activity2.onCreate()
            fragment2.onAttach(Activity2::class)

            expect(activity2.viewModel).to.be.an.instanceof(Activity2ViewModel::class.java)
            expect(fragment2.viewModel).to.be.an.instanceof(FragmentViewModel::class.java)
            expect(fragment2.viewModel).not.to.equal(fragment1.viewModel)
            expect(fragment1.viewModel.logger).not.to.satisfy { it === activity1.logger }

            fragment1.onDetach()

            expectToThrow<IllegalStateException> { Kadi.getScope(Fragment::class) }
            expect(Kadi.getScope(Activity1::class).get<Activity1ViewModel>()).to.equal(activity1.viewModel)

            fragment2.onDetach()

            activity2.onDestroy()
            activity1.onDestroy()

            expectToThrow<IllegalStateException> { Kadi.getScope(Fragment::class) }

            val database = appScope.get<IDatabase>() as Database
            expect(database.isClosed).to.be.`false`
            Kadi.getScope(Application::class).close()
            expect(database.isClosed).to.be.`false`
        }
    }
})
