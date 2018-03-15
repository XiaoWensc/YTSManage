package asuper.yt.cn.supermarket;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import asuper.yt.cn.supermarket.modules.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by zengxiaowen on 2017/12/5.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest{

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onTest(){
        while (true){
            onView(withId(44)).perform(click());
            onView(withId(45)).perform(click());
            onView(withId(47)).perform(click());
            onView(withId(48)).perform(click());
        }
    }
}
