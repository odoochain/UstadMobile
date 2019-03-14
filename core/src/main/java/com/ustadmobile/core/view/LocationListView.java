package com.ustadmobile.core.view;

import com.ustadmobile.core.db.UmProvider;
import com.ustadmobile.lib.db.entities.LocationWithSubLocationCount;


/**
 * Core View. Screen is for LocationList's View
 */
public interface LocationListView extends UstadView {


    // This defines the view name that is an argument value in the go() in impl.
    String VIEW_NAME = "LocationList";

    //Any argument keys:

    /**
     * Method to finish the screen / view.
     */
    void finish();


    /**
     * Sets the given provider to the view's provider adapter.
     *
     * @param listProvider The provider to set to the view
     */
    void setListProvider(UmProvider<LocationWithSubLocationCount> listProvider);


}

