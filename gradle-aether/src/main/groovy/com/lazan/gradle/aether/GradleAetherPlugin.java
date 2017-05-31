package com.lazan.gradle.aether;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradleAetherPlugin implements Plugin<Project> {
	public void apply(Project project) {
		try {
			RepositorySystem system = newRepositorySystem();
			RepositorySystemSession session = newRepositorySystemSession(project, system);
	
			Artifact artifact = new DefaultArtifact("org.wildfly.swarm:undertow:2017.5.0");
	
			DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
	
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
			collectRequest.setRepositories(Arrays.asList(mavenCentralRepository()));
	
			DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
	
			List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest)
					.getArtifactResults();
	
			for (ArtifactResult artifactResult : artifactResults) {
				System.out.println(artifactResult.getArtifact() + " resolved to " + artifactResult.getArtifact().getFile());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RepositorySystem newRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				exception.printStackTrace();
			}
		});

		return locator.getService(RepositorySystem.class);
	}

	protected DefaultRepositorySystemSession newRepositorySystemSession(Project project, RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		File dir = new File(project.getBuildDir(), "aether/repository");
		LocalRepository localRepo = new LocalRepository(dir);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		// session.setTransferListener(new ConsoleTransferListener());
		// session.setRepositoryListener(new ConsoleRepositoryListener());

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

	protected RemoteRepository mavenCentralRepository() {
		return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
	}
}