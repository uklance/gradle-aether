package com.lazan.gradle.aether;

import java.io.File;
import java.util.ArrayList;
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
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleDependency;

public class AetherDependencyHandler {
	private final Project project;
	private final AetherRepositoryHandler repositoryHandler;
	
	public AetherDependencyHandler(Project project, AetherRepositoryHandler repositoryHandler) {
		super();
		this.project = project;
		this.repositoryHandler = repositoryHandler;
	}
	
	public void add(Configuration configuration, String notation) {
		try {
			RepositorySystem system = newRepositorySystem();
			RepositorySystemSession session = newRepositorySystemSession(project, system);
	
			Artifact artifact = new DefaultArtifact(notation);
	
			DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
	
			List<RemoteRepository> repositories = new ArrayList<>();
			for (AetherRepository repository : repositoryHandler.getRepositories()) {
				RemoteRepository remoteRepository 
					= new RemoteRepository.Builder(repository.getId(), repository.getType(), repository.getUrl()).build();
				repositories.add(remoteRepository);
			}
				
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
			collectRequest.setRepositories(repositories);
	
			DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
	
			List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest)
					.getArtifactResults();
	
			for (ArtifactResult artifactResult : artifactResults) {
				Artifact current = artifactResult.getArtifact();
				String resultNotation = String.format("%s:%s:%s", current.getGroupId(), current.getArtifactId(), current.getVersion());
				if (current.getClassifier() != null) {
					resultNotation = String.format("%s:%s", resultNotation, current.getClassifier());
				}
				if (current.getExtension() != null && current.getExtension().isEmpty() && !"jar".equals(current.getExtension())) {
					resultNotation = String.format("%s@%s", resultNotation, current.getExtension());
				}				
				ModuleDependency dependency = (ModuleDependency) project.getDependencies().create(resultNotation);
				dependency.setTransitive(false);
				configuration.getDependencies().add(dependency);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void add(String configuration, String notation) {
		add(project.getConfigurations().getByName(configuration), notation);
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
}
