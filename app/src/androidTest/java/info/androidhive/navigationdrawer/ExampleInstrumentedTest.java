package info.androidhive.navigationdrawer;

import android.content.Context;

// ✅ FIXED: Updated to AndroidX Test Imports
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        // ✅ FIXED: Updated method call for AndroidX
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Note: This checks if the package name is correct.
        // Based on your logs, your app ID is "com.loyaladmin.navigationdrawer"
        assertEquals("com.loyaladmin.navigationdrawer", appContext.getPackageName());
    }
}