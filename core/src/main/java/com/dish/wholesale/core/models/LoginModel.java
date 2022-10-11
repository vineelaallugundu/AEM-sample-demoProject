package com.dish.wholesale.core.models;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.api.resource.Resource;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class LoginModel {
    
	@ValueMapValue
    protected String username;

	@ValueMapValue
    protected String password;
	
	@ValueMapValue
    protected String buttontext;



    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getButtontext() {
        return buttontext;
    }
	
}
