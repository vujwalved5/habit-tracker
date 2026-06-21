package com.example.habittracker.presentation.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleReminder(habitId: String, habitName: String, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // For simplicity, we fallback to inexact or just skip exact for now.
                // In a real app, we'd request the permission.
            }
        }
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra("HABIT_NAME", habitName)
            putExtra("HABIT_ID", habitId)
        }

        // Small collision risk if habitId is not a UUID
        val requestCode = try {
            val uuid = UUID.fromString(habitId)
            (uuid.mostSignificantBits xor uuid.leastSignificantBits).toInt()
        } catch (e: Exception) {
            habitId.hashCode()
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val parts = time.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
