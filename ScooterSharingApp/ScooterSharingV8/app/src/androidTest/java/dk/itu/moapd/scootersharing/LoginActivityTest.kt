package dk.itu.moapd.scootersharing

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.activities.login.LoginActivity
import dk.itu.moapd.scootersharing.util.hasTextInputLayoutErrorText
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

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
    fun loginSuccess() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signin_button)).perform(click())
        Thread.sleep(2000)
        assert(FirebaseAuth.getInstance().currentUser!!.displayName == "Joachim")
    }

    @Test
    @FlakyTest
    fun loginWrongEmail() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.email_editTextField)).perform(typeText("hello@man.dk"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signin_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.email_textField)).check(matches(hasTextInputLayoutErrorText(context.resources.getString(R.string.email_error))))
        onView(withId(R.id.password_textField)).check(matches(hasTextInputLayoutErrorText(context.resources.getString(R.string.password_error))))
        assert(FirebaseAuth.getInstance().currentUser == null)
    }

    @Test
    @FlakyTest
    fun registerUsingEmptyName() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText(""))
        onView(withId(R.id.email_editTextField)).perform(typeText("ole@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.name_textField)).check(matches(hasTextInputLayoutErrorText(context.resources.getString(R.string.name_error))))
        assert(FirebaseAuth.getInstance().currentUser == null)
    }

    @Test
    @FlakyTest
    fun registerTakenEmail() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Pingu"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@kelsen.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.email_textField)).check(matches(hasTextInputLayoutErrorText(context.resources.getString(R.string.email_error))))
        onView(withId(R.id.password_textField)).check(matches(hasTextInputLayoutErrorText(context.resources.getString(R.string.password_error))))
        assert(FirebaseAuth.getInstance().currentUser == null)
    }

    @Test
    @FlakyTest
    fun registerSuccess() {
        FirebaseAuth.getInstance().signOut()
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.name_editTextField)).perform(typeText("Hello there"))
        onView(withId(R.id.email_editTextField)).perform(typeText("joachim@man.nu"))
        onView(withId(R.id.password_editTextField)).perform(typeText("kelsen1234"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.signup_button)).perform(click())

        //Cleanup
        Thread.sleep(3000)
        FirebaseAuth.getInstance().currentUser!!.delete()
        Thread.sleep(5000)

        //Verify
        assert(FirebaseAuth.getInstance().currentUser == null)
        onView(withId(R.id.bottom_nav)).check(matches(ViewMatchers.isDisplayed()))
    }
}