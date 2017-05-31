package com.lazan.gradle.aether;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class AetherDependencyHandler {
	private final Project project;
	private final AetherRepositoryHandler repositoryHandler;
	
	public AetherDependencyHandler(Project project, AetherRepositoryHandler repositoryHandler) {
		super();
		this.project = project;
		this.repositoryHandler = repositoryHandler;
	}
	
	public void add(Configuration configuration, String notation) {
		
	}

}
