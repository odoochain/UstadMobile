package com.ustadmobile.core.view

import androidx.paging.DataSource
import com.ustadmobile.door.DoorMutableLiveData
import com.ustadmobile.lib.db.entities.ClazzWorkQuestionAndOptionWithResponse
import com.ustadmobile.lib.db.entities.ClazzWorkWithSubmission
import com.ustadmobile.lib.db.entities.CommentsWithPerson
import com.ustadmobile.lib.db.entities.ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer


interface ClazzWorkDetailOverviewView: UstadDetailView<ClazzWorkWithSubmission> {

    var clazzWorkContent
            :DataSource.Factory<Int, ContentEntryWithParentChildJoinAndStatusAndMostRecentContainer>?

    var editableQuizQuestions
            : DoorMutableLiveData<List<ClazzWorkQuestionAndOptionWithResponse>>?

    var viewOnlyQuizQuestions
            : DoorMutableLiveData<List<ClazzWorkQuestionAndOptionWithResponse>>?
    var timeZone: String
    var clazzWorkPublicComments: DataSource.Factory<Int, CommentsWithPerson>?
    var clazzWorkPrivateComments: DataSource.Factory<Int, CommentsWithPerson>?
    var isStudent : Boolean
    var showMarking: Boolean
    var showFreeTextSubmission: Boolean
    var showSubmissionButton: Boolean
    var showQuestionHeading: Boolean
    var showSubmissionHeading: Boolean
    var showPrivateComments: Boolean
    var showNewPrivateComment: Boolean


    companion object {

        const val VIEW_NAME = "ClazzWorkWithSubmissionDetailView"

    }

}