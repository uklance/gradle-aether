package com.lazan.gradle.aether;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradleAetherPlugin implements Plugin<Project> {
	public void apply(Project project) {
		AetherModel model = new AetherModel(project);
		project.getExtensions().add("aether", model);
	}
}