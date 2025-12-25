package com.codewithdipesh.habitized.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.codewithdipesh.habitized.di.WidgetRepositoryEntryPoint
import com.codewithdipesh.habitized.widget.data.HabitWidgetDataStore
import com.codewithdipesh.habitized.widget.elements.LoadingContent
import com.codewithdipesh.habitized.widget.elements.OverAllHabitWidgetContent
import com.codewithdipesh.habitized.widget.elements.WeeklyHabitWidgetContent
import dagger.hilt.android.EntryPointAccessors

class WeeklyHabitWidget (): GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val habitId = HabitWidgetDataStore.getHabitIdForWidget(context, appWidgetId)

        if (habitId == null) {
            provideContent {
                LoadingContent()
            }
            return
        }

        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetRepositoryEntryPoint::class.java
        )
        val repository = entryPoint.habitWidgetRepository()
        val habitInfo = repository.getHabit(habitId, WidgetType.Weekly)

        if (habitInfo == null) {
            provideContent {
                LoadingContent()
            }
            return
        }

        provideContent {
            WeeklyHabitWidgetContent(habitInfo)
        }
    }
}


class MonthlyHabitWidget() : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        var habitId = HabitWidgetDataStore.getHabitIdForWidget(context, appWidgetId)
        if (habitId == null) {
            provideContent {
                LoadingContent()
            }
            return
        }

        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetRepositoryEntryPoint::class.java
        )
        val repository = entryPoint.habitWidgetRepository()
        val habitInfo = repository.getHabit(habitId, WidgetType.Overall)

        if (habitInfo == null) {
            provideContent {
                LoadingContent()
            }
            return
        }

        provideContent {
            OverAllHabitWidgetContent(habitInfo)
        }
    }
}
