package asuper.yt.cn.supermarket;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressButton;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by zengxiaowen on 2017/12/5.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityInstrumentationTest{
    private static final String userName = "c";
    private static final String userPas = "11";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);


    @Test
    public void onStart(){
        int i = 10;
        do {
            onView(withId(R.id.email)).perform(typeText(userName), closeSoftKeyboard()); //line 1
            onView(withId(R.id.password)).perform(typeText(userPas), closeSoftKeyboard()); //line 2
//            onView(withId(R.id.email_sign_in_button)).perform(click()); //line 3
            i--;
        } while (i<0);
    }
}
