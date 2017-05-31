package com.lazan.gradle.aether;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

public class AetherModel {
	private final AetherDependencyHandler dependencyHandler;
	private final AetherRepositoryHandler repositoryHandler;
	
	public AetherModel(Project project) {
		super();
		this.repositoryHandler = new AetherRepositoryHandler();
		this.dependencyHandler = new AetherDependencyHandler(project, repositoryHandler);
	}
	public void repositories(Action<AetherRepositoryHandler> action) {
		action.execute(repositoryHandler);
	}
	
	@SuppressWarnings("rawtypes")
	public void repositories(Closure closure) {
		repositories(ConfigureUtil.configureUsing(closure));
	}
	
	public void dependencies(Action<AetherDependencyHandler> action) {
		action.execute(dependencyHandler);
	}
	
	@SuppressWarnings("rawtypes")
	public void dependencies(Closure closure) {
		dependencies(ConfigureUtil.configureUsing(closure));
	}	
}
