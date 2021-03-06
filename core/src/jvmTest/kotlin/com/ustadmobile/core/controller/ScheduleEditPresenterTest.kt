package com.ustadmobile.core.controller

import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.ustadmobile.core.generated.locale.MessageID
import com.ustadmobile.core.impl.UstadMobileSystemImpl
import com.ustadmobile.core.util.UstadTestRule
import com.ustadmobile.core.view.ScheduleEditView
import com.ustadmobile.door.DoorLifecycleObserver
import com.ustadmobile.door.DoorLifecycleOwner
import com.ustadmobile.door.util.systemTimeInMillis
import com.ustadmobile.lib.db.entities.Schedule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

class ScheduleEditPresenterTest {

    @JvmField
    @Rule
    var ustadTestRule = UstadTestRule()

    private lateinit var di: DI

    private lateinit var mockView: ScheduleEditView

    private lateinit var mockLifecycleOwner: DoorLifecycleOwner

    @Before
    fun setup() {
        di = DI {
            import(ustadTestRule.diModule)
        }

        mockView = mock {  }

        mockLifecycleOwner = mock {
            on { currentState }.thenReturn(DoorLifecycleObserver.RESUMED)
        }
    }

    @Test
    fun givenScheduleHasNoStartTime_whenClickSave_thenShouldShowError() {
        val scheduleEditPresenter = ScheduleEditPresenter(Any(), mapOf(), mockView,
                    di, mockLifecycleOwner)

        scheduleEditPresenter.onCreate(null)

        scheduleEditPresenter.handleClickSave(Schedule().apply {
            sceduleStartTime = 0L
            scheduleEndTime = systemTimeInMillis()
        })

        val expectedErrMessage = di.direct.instance<UstadMobileSystemImpl>()
                .getString(MessageID.field_required_prompt, Any())
        verify(mockView).fromTimeError = eq(expectedErrMessage)
    }

    @Test
    fun givenScheduleHasNoEndTime_whenClickSave_thenShouldShowError() {
        val scheduleEditPresenter = ScheduleEditPresenter(Any(), mapOf(), mockView,
                di, mockLifecycleOwner)

        scheduleEditPresenter.onCreate(null)

        scheduleEditPresenter.handleClickSave(Schedule().apply {
            sceduleStartTime = systemTimeInMillis()
            scheduleEndTime = 0L
        })

        val expectedErrMessage = di.direct.instance<UstadMobileSystemImpl>()
                .getString(MessageID.field_required_prompt, Any())
        verify(mockView).toTimeError = eq(expectedErrMessage)
    }

    @Test
    fun givenScheduleStartAfterEndTime_whenClickSave_thenShouldShowError() {
        val scheduleEditPresenter = ScheduleEditPresenter(Any(), mapOf(), mockView,
                di, mockLifecycleOwner)

        scheduleEditPresenter.onCreate(null)

        scheduleEditPresenter.handleClickSave(Schedule().apply {
            sceduleStartTime = systemTimeInMillis()
            scheduleEndTime = systemTimeInMillis() - 1000
        })

        val expectedErrMessage = di.direct.instance<UstadMobileSystemImpl>()
                .getString(MessageID.end_is_before_start_error, Any())
        verify(mockView).toTimeError = eq(expectedErrMessage)
    }

    @Test
    fun givenValidSchedule_whenClickSave_thenShouldFinishWithResult() {
        val scheduleEditPresenter = ScheduleEditPresenter(Any(), mapOf(), mockView,
                di, mockLifecycleOwner)

        scheduleEditPresenter.onCreate(null)

        var validSchedule =Schedule().apply {
            scheduleDay = Schedule.DAY_SUNDAY
            sceduleStartTime = systemTimeInMillis()
            scheduleEndTime = systemTimeInMillis() + 1000
        }

        scheduleEditPresenter.handleClickSave(validSchedule)

        verify(mockView).finishWithResult(argWhere {
            it.first() == validSchedule
        })
    }

}