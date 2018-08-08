package com.ustadmobile.port.gwt.client.application.base;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.view.BasePointView;
import com.ustadmobile.port.gwt.client.application.ApplicationPresenter;
import com.ustadmobile.port.gwt.client.application.about.AboutPresenter;
import com.ustadmobile.port.gwt.client.place.NameTokens;
import java.util.Iterator;
import java.util.Set;
import java.util.Hashtable;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.Slot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;


/**
 * This is the child presenter of Application Presenter.
 * It uses its parent's presenter's (ApplicationPresenter) Slot to 
 *  reveal itself.
 *  
 *  extends Presenter<HomePresenter.MyView, ...  <--This defines the
 *  ApplicationPresenter superclass. Those interfaces need to be defined
 *  into the class.
 *  
 *  This presenter class also implements BaseUiHandler this isn't tied to 
 *  Core, but is very GWT specific. Right now every other UI Handler is tied 
 *  to the Core. In the future if there is any GWT only handling in the UI,
 *  it would go there. 
 *  
 *  BasePresenter for BasePointController
 * @author varuna
 *
 */
public class BasePresenter 
  extends Presenter<BasePresenter.MyView, BasePresenter.MyProxy> 
	implements BaseUiHandlers{
	
	/**
	 * We are creating this class to be the link between the Core Controller. This 
	 * handler class  will be used as a handler in the Base View. 
	 * 
     * This class extends the core controller/presenter and implements UiHandler
     * This class is extended by BasePresenter' view interface so it indirectly also implements UiHandler
     * 
     * @author varuna
     *
     */
    public class CoreBasePointPresenterHandler 	
    								extends com.ustadmobile.core.controller.BasePointController 
    								implements UiHandlers {

		public CoreBasePointPresenterHandler(Object context, BasePointView view) {
			//This calls the Core Presenter's constructor.
			super(context, view);
		}

		/**
		 * This method goes is tied to the feed button. 
		 */
		public void feedClicked() {
			GWT.log("BasePresenter:CoreBasePointPresenterHandler.feedClicked()");
			GWT.log("Replace this. For testing: Going to About page.");
			
			UstadMobileSystemImpl impl = UstadMobileSystemImpl.getInstance();
			Hashtable args = new Hashtable();
			impl.go(NameTokens.ABOUT, args, placeManager);
		}
		
    }
    
    /**
     * This is the BasePresenter's View Interface. It links the Core BasePointView to 
     * this BasePresenter. 
     * 
     * The methods here will be overriden in GWT's View : BaseView.
     * 
     * @author Varuna Singh
     *
     */
    interface MyView extends 	View, 
								HasUiHandlers<CoreBasePointPresenterHandler>, 
								com.ustadmobile.core.view.BasePointView {
    	
    	/**
    	 * Simple method to reload tabs.
    	 */
    	public void refreshTabs();

    }
    
    /**
     * The annotation NakeToken assigns this Module and Presenter 
     * (here BasePresenter) to that NameToken. 
     * A presenter having a NameToken is a place. 
     * Hence this Module+Presenter+View becomes a place to call go() on.
     * 
     * The NoGatekeeper presenter is me testing login access. 
     * 
     * The ProxyStandard is GWTP specific. 
     * 
     * @author Varuna Singh
     *
     */
    @ProxyStandard
    @NameToken(NameTokens.BASE)
    @NoGatekeeper
    interface MyProxy extends ProxyPlace<BasePresenter> {
    }
    
    //The placeManager is useful to go from place to place.
    private PlaceManager placeManager;
    private Hashtable args;
    private com.ustadmobile.core.controller.BasePointController mController;
    
    private AboutPresenter aboutPresenter;
    
    //Main Tab Content slot
    //public static final NestedSlot SLOT_TAB = new NestedSlot();
    public static final Slot SLOT_TAB = new Slot();

    /**
     * This is the GWT presenter's constructor. 
     * This gets first as part of generating the revealing the Base Place.
     * 
     * @param eventBus
     * @param view
     * @param proxy
     * @param placeManager The placMannager is sort of like the context here that is 
     * passed as you go from place to place so that we don't re create it. 
     */
    @Inject
    BasePresenter(EventBus eventBus, 
    		MyView view, 
    		MyProxy proxy, 
    		PlaceManager placeManager) {
        super(eventBus, view, proxy, 
        		ApplicationPresenter.SLOT_CONTENT
        		);
        GWT.log("BasePresenter()");
        this.placeManager = placeManager;
        
        //Create an instance of the CorePresenterHandler in the GWT Presenter's constructor.
        mController = new CoreBasePointPresenterHandler(placeManager, view);
        
        // We are setting the CorePresetnerHandler to be the view's UI Handler.
        // This is more useful in the View itself. 
        getView().setUiHandlers((CoreBasePointPresenterHandler) mController);
        
    }
    
    /**
     * This is the second thing that gets called from GWTP's 
     * Presenter class whenever we go to a new Place. 
     * 
     * The purpose and usage of this method is to place this presenter in
     *  the slot of the Main Application View before the rendering happens.
     */
    @Override
	protected void onBind() {
		// TODO Check if useful to: super.onBind() ?
    	GWT.log("BasePresenter().onBind()");
		setInSlot(SLOT_TAB, aboutPresenter);
	}    
    
    /**
     * This is the third methods that gets called 
     * from the GWTP when a place is loaded. 
     * This method here gets the arguments from the request,
     * and calls the Core contructor's onCreate method.
     * 
     * This is loaded again when returning to the Place as well.  
     * This is an override from GWTP's Presenter class. 
     */
	@Override
	public void prepareFromRequest(PlaceRequest request) {
		super.prepareFromRequest(request);
		Set<String> requestArgNames = request.getParameterNames();
		this.args = new Hashtable();
		Iterator<String> it = requestArgNames.iterator();
		while(it.hasNext()){
			String key = it.next();
			this.args.put(key, request.getParameter(key, ""));
		}
		GWT.log("BasePresenter: prepareFromRequest(): Argument creation done.");
		mController.onCreate(args, null);
	}

    /* Any UI Handler Overrides */
	@Override
	public void baseDummyHandler() {
		GWT.log("BasePresenter.baseDummyHandler() : Alora!");
	}
	

}
