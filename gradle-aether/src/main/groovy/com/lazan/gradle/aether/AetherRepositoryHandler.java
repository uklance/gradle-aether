package com.lazan.gradle.aether;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Action;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

public class AetherRepositoryHandler {
	private List<AetherRepository> repositories = new ArrayList<>();

	public void repository(Action<AetherRepository> action) {
		AetherRepository repository = new AetherRepository();
		repositories.add(repository);
		action.execute(repository);
	}
	
	@SuppressWarnings("rawtypes")
	public void repository(Closure closure) {
		repository(ConfigureUtil.configureUsing(closure));
	}
	
	public void mavenCentral() {
		repository(new Action<AetherRepository>() {
			@Override
			public void execute(AetherRepository repository) {
				repository.setId("central");
				repository.setType("default");
				repository.setUrl("http://central.maven.org/maven2/");
			}
		});
	}
	
	public List<AetherRepository> getRepositories() {
		return repositories;
	}
}
