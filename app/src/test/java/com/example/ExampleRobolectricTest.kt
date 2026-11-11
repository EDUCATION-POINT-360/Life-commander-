package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.ActivityCategory
import com.example.data.models.PriorityLevel
import com.example.data.models.RoutineActivity
import com.example.localization.AppLanguage
import com.example.localization.StringsProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Life Commander app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Life Commander", appName)
    }

    @Test
    fun `routine activity duration cross midnight calculation is correct`() {
        // Sleep from 11:00 PM (1380m) to 06:00 AM (360m)
        val sleepActivity = RoutineActivity(
            title = "Night Sleep",
            startTimeMinutes = 1380,
            endTimeMinutes = 360,
            category = ActivityCategory.SLEEP,
            priority = PriorityLevel.HIGH
        )
        // (1440 - 1380) + 360 = 60 + 360 = 420 minutes = 7 hours
        assertEquals(420, sleepActivity.durationMinutes)
    }

    @Test
    fun `bilingual strings provider returns English and Urdu properly`() {
        val enDashboard = StringsProvider.get("dashboard", AppLanguage.ENGLISH)
        val urDashboard = StringsProvider.get("dashboard", AppLanguage.URDU)
        assertEquals("Dashboard", enDashboard)
        assertEquals("ڈیش بورڈ", urDashboard)
    }

    @Test
    fun `time formatting formats minutes to 12 hour AM PM format accurately`() {
        assertEquals("06:00 AM", RoutineActivity.formatMinutesToTime(360))
        assertEquals("12:00 PM", RoutineActivity.formatMinutesToTime(720))
        assertEquals("11:00 PM", RoutineActivity.formatMinutesToTime(1380))
    }
}
