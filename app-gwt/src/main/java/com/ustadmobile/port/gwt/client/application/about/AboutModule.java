/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2017 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.ustadmobile.port.gwt.client.application.about;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * This is a sub Module (parent: ApplicationModule). 
 * It is bound in its parent's (ApplicationModule) configure().
 * @author varuna
 *
 */
public class AboutModule extends AbstractPresenterModule {
	
	//Initialising the Module, setting Presetners and Views
    @Override
    protected void configure() {
        bindPresenter(AboutPresenter.class, 
        		AboutPresenter.MyView.class, 
        		AboutView.class, 
        		AboutPresenter.MyProxy.class);
    }
}
