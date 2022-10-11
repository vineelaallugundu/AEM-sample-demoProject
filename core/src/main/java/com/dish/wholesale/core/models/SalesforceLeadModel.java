package com.dish.wholesale.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.dish.wholesale.core.configs.HcaptchaConfig;

@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/sales-lead-form" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class SalesforceLeadModel {

	@Inject
	private String firstNameLabel;

	@Inject
	private String lastNameLabel;

	@Inject
	private String phone;

	@Inject
	private String title;

	@Inject
	private String email;

	@Inject
	private String company;

	@Inject
	private String street;

	@Inject
	private String zip;

	@Inject
	private String city;

	@Inject
	private String country;
	
	@Inject
	private String state;

	@Inject
	private String productInterest;

	@Inject
	private String salesforceUrl;

	@Inject
	private String salesforceOid;

	@Inject
	private String salesforceLeadSource;

	@Inject
	private String startDate;

	@Inject
	private String description;

	@Inject
	private String productInterestID;

	@Inject
	private String desiredSpectrum;

	@Inject
	private String usageType;

	@Inject
	private String estimatedRadius;

	@Inject
	private String intendedLength;

	@Inject
	private String intendedUse;

	@Inject
	private String startDateID;
	
	@OSGiService
	private HcaptchaConfig hcaptchaConfig;

	public String getSalesforceUrl() {
		return salesforceUrl;
	}

	public String getSalesforceOid() {
		return salesforceOid;
	}

	public String getFirstNameLabel() {
		return firstNameLabel;
	}

	public String getLastNameLabel() {
		return lastNameLabel;
	}

	public String getEmail() {
		return email;
	}

	public String getCompany() {
		return company;
	}

	public String getStreet() {
		return street;
	}

	public String getZip() {
		return zip;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getState() {
		return state;
	}

	public String getPhone() {
		return phone;
	}

	public String getTitle() {
		return title;
	}

	public String getProductInterest() {
		return productInterest;
	}

	public String getSalesforceLeadSource() {
		return salesforceLeadSource;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getDescription() {
		return description;
	}

	public String getProductInterestID() {
		return productInterestID;
	}

	public String getDesiredSpectrum() {
		return desiredSpectrum;
	}

	public String getUsageType() {
		return usageType;
	}

	public String getEstimatedRadius() {
		return estimatedRadius;
	}

	public String getIntendedLength() {
		return intendedLength;
	}

	public String getIntendedUse() {
		return intendedUse;
	}

	public String getStartDateID() {
		return startDateID;
	}
	
	public String getHcaptchaSiteKey() {
		return hcaptchaConfig.getHcaptchaSiteKey();
	}

}
