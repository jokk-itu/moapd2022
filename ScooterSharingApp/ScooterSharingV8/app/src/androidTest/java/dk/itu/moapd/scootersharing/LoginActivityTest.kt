package dk.itu.moapd.scootersharing

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dk.itu.moapd.scootersharing.activities.login.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setup() {
        scenario = launchActivity()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun loginSuccess() {
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signin_button)).perform(click())
        assert(FirebaseAuth.getInstance().currentUser!!.displayName == "Joachim")
    }

    @Test
    fun loginWrongEmailAndPassword() {

    }

    @Test
    fun registerTakenEmail() {
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
    }

    @Test
    fun registerSuccess() {
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signin_button)).perform(click())
        FirebaseAuth.getInstance().currentUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName("Joachim").build())
    }

}