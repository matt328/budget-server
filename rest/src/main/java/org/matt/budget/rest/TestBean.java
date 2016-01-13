package org.matt.budget.rest;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;

import org.matt.budget.persistence.AccountService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Startup
@Named
public class TestBean {

	@Inject
	private AccountService accountService;

	@PostConstruct
	public void initialize() {
		log.trace("Trace");
		log.debug("Initialized TestBean, accountService: {}", accountService);
		log.info("Info");
		log.warn("Warn");
		log.error("Error");
		
	}

}
