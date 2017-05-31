package com.lazan.gradle.aether;

import org.gradle.api.Action;
import org.gradle.api.Project;

public class AetherModel {
	private final Project project;
	private final AetherDependencyHandler dependencyHandler;
	private final AetherRepositoryHandler repositoryHandler;
	
	public AetherModel(Project project) {
		super();
		this.project = project;
		this.repositoryHandler = new AetherRepositoryHandler(project);
		this.dependencyHandler = new AetherDependencyHandler(project, repositoryHandler);
	}
	public void repositories(Action<AetherRepositoryHandler> action) {
		action.execute(repositoryHandler);
	}
	public void dependencies(Action<AetherDependencyHandler> action) {
		action.execute(dependencyHandler);
	}
}
