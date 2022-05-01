package dk.itu.moapd.scootersharing

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.activities.login.LoginActivity
import dk.itu.moapd.scootersharing.fragments.ScooterListFragment
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RentScooterTest {

    private lateinit var scenario: ActivityScenario<LoginActivity>
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        scenario = launchActivity()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    @FlakyTest
    fun startRideFailBecauseOfActiveRide() {
        //Login
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.email_editTextField))
            .perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField))
            .perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signin_button)).perform(click())
        Thread.sleep(4000)

        //Verify
        for (i in 0..6) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isNotEnabled())))
        }
    }

    @Test
    @FlakyTest
    fun startRideSuccess() {
        //Arrange
        val scooterId = 2L
        val recyclerViewPosition = 0

        //Register
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(4000)

        //Scan
        onView(withId(R.id.scooter_recycler_view))
            .perform(
                actionOnItemAtPosition<ScooterListFragment.ScooterHolder>(
                    recyclerViewPosition,
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View>? = null
                        override fun getDescription(): String = ""
                        override fun perform(uiController: UiController, view: View) {
                            val startRideButton = view.findViewById<Button>(R.id.startRide)
                            startRideButton.performClick()
                        }
                    }
                )
            )
        Thread.sleep(1000)
        pressBack()

        //Send Start Ride Broadcast
        val startRideIntent = Intent(R.string.start_ride_event.toString())
        startRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(startRideIntent)
        Thread.sleep(5000)

        //Verify
        for (i in 0..5) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isNotEnabled())))
        }

        //Cleanup
        val endRideIntent = Intent(R.string.end_ride_event.toString())
        endRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(endRideIntent)
        Thread.sleep(5000)

        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)
    }

    @Test
    @FlakyTest
    fun startRideFailBecauseOfNonAvailableScooterId() {
        //Arrange
        val scooterId = 1L
        val recyclerViewPosition = 0

        //Register
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(4000)

        //Scan
        onView(withId(R.id.scooter_recycler_view))
            .perform(
                actionOnItemAtPosition<ScooterListFragment.ScooterHolder>(
                    recyclerViewPosition,
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View>? = null
                        override fun getDescription(): String = ""
                        override fun perform(uiController: UiController, view: View) {
                            val startRideButton = view.findViewById<Button>(R.id.startRide)
                            startRideButton.performClick()
                        }
                    }
                )
            )
        Thread.sleep(1000)
        pressBack()

        //Send Start Ride Broadcast
        val startRideIntent = Intent(R.string.start_ride_event.toString())
        startRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(startRideIntent)
        Thread.sleep(5000)

        //Verify
        for (i in 0..5) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isEnabled())))
        }

        //Cleanup
        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)
    }

    @Test
    @FlakyTest
    fun startRideFailBecauseOfInvalidQRCode() {
        //Arrange
        val scooterId = "invalid scooterId"
        val recyclerViewPosition = 0

        //Register
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(4000)

        //Scan
        onView(withId(R.id.scooter_recycler_view))
            .perform(
                actionOnItemAtPosition<ScooterListFragment.ScooterHolder>(
                    recyclerViewPosition,
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View>? = null
                        override fun getDescription(): String = ""
                        override fun perform(uiController: UiController, view: View) {
                            val startRideButton = view.findViewById<Button>(R.id.startRide)
                            startRideButton.performClick()
                        }
                    }
                )
            )
        Thread.sleep(1000)
        pressBack()

        //Send Start Ride Broadcast
        val startRideIntent = Intent(R.string.start_ride_event.toString())
        startRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(startRideIntent)
        Thread.sleep(5000)

        //Verify
        for (i in 0..5) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isEnabled())))
        }

        //Cleanup
        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)
    }

    @Test
    @FlakyTest
    fun startRideThenEndRideImmediately() {
        //Arrange
        val scooterId = 2L
        val recyclerViewPosition = 0

        //Register
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(4000)

        //Scan
        onView(withId(R.id.scooter_recycler_view))
            .perform(
                actionOnItemAtPosition<ScooterListFragment.ScooterHolder>(
                    recyclerViewPosition,
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View>? = null
                        override fun getDescription(): String = ""
                        override fun perform(uiController: UiController, view: View) {
                            val startRideButton = view.findViewById<Button>(R.id.startRide)
                            startRideButton.performClick()
                        }
                    }
                )
            )
        Thread.sleep(1000)
        pressBack()

        //Send Start Ride Broadcast
        val startRideIntent = Intent(R.string.start_ride_event.toString())
        startRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(startRideIntent)
        Thread.sleep(5000)

        // Verify Current Ride in Account's RideList
        onView(withId(R.id.account)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.ride_recycler_view)).check { view, noViewFoundException ->
            if(view !is RecyclerView)
                throw noViewFoundException

            if(view.adapter == null)
                throw noViewFoundException

            for(i in 0..view.adapter!!.itemCount) {
                val viewHolder = view.findViewHolderForAdapterPosition(i)
                val endRideButton = viewHolder!!.itemView.findViewById<Button>(R.id.ride_end)
                if(endRideButton.isEnabled) {
                    return@check
                }
            }
            throw noViewFoundException
        }

        //Send End Ride Broadcast
        val endRideIntent = Intent(R.string.end_ride_event.toString())
        endRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(endRideIntent)
        Thread.sleep(5000)

        //Verify
        onView(withId(R.id.scooters)).perform(click())
        Thread.sleep(1000)
        for (i in 0..5) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isEnabled())))
        }

        //Cleanup
        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)
    }

    @Test
    @FlakyTest
    fun startRideThenEndRideImmediatelyFailBecauseQRCodeIsInvalid() {
        //Arrange
        val scooterId = 2L
        val recyclerViewPosition = 0

        //Register
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(4000)

        //Scan
        onView(withId(R.id.scooter_recycler_view))
            .perform(
                actionOnItemAtPosition<ScooterListFragment.ScooterHolder>(
                    recyclerViewPosition,
                    object : ViewAction {
                        override fun getConstraints(): Matcher<View>? = null
                        override fun getDescription(): String = ""
                        override fun perform(uiController: UiController, view: View) {
                            val startRideButton = view.findViewById<Button>(R.id.startRide)
                            startRideButton.performClick()
                        }
                    }
                )
            )
        Thread.sleep(1000)
        pressBack()

        //Send Start Ride Broadcast
        val startRideIntent = Intent(R.string.start_ride_event.toString())
        startRideIntent.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(startRideIntent)
        Thread.sleep(5000)

        //Send End Ride Broadcast
        val endRideIntent = Intent(R.string.end_ride_event.toString())
        endRideIntent.putExtra("scooterId", "invalid scooterId")
        LocalBroadcastManager.getInstance(context).sendBroadcast(endRideIntent)
        Thread.sleep(5000)

        //Verify
        for (i in 0..5) {
            onView(withId(R.id.scooter_recycler_view))
                .perform(scrollToPosition<ScooterListFragment.ScooterHolder>(i))
                .check(matches(hasDescendant(isNotEnabled())))
        }

        //Cleanup
        val endRideIntent1 = Intent(R.string.end_ride_event.toString())
        endRideIntent1.putExtra("scooterId", scooterId)
        LocalBroadcastManager.getInstance(context).sendBroadcast(endRideIntent1)
        Thread.sleep(5000)

        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)
    }
}