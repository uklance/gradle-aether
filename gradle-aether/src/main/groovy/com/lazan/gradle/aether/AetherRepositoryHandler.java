package com.lazan.gradle.aether;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Action;
import org.gradle.api.Project;

public class AetherRepositoryHandler {
	private final Project project;
	private List<AetherRepository> repositories = new ArrayList<>();

	public AetherRepositoryHandler(Project project) {
		super();
		this.project = project;
	}
	
	public void repository(Action<AetherRepository> action) {
		AetherRepository repository = new AetherRepository();
		repositories.add(repository);
		action.execute(repository);
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
