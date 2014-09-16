package com.yoshtec.owl.jcodegen.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slf4j.impl.StaticLoggerBinder;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.yoshtec.owl.jcodegen.Codegen;
import com.yoshtec.owl.jcodegen.CodegenException;

/**
 * Maven Plugin to generate Java classes from OWL file
 * 
 * @author Flavio Pompermaier (pompermaier@okkam.it)
 * 
 */
@Mojo(name = "owl2java", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class JaobOwl2JavaMojo extends AbstractMojo {
	@Parameter(property = "output", defaultValue = "${project.build.directory}/generated-sources/owl2java")
	private File output;
	@Parameter(property = "packageName")
	private String packageName;
	@Parameter(property = "ontologyUri")
	private String ontologyUri;
	@Parameter(property = "ontologyNs")
	private String ontologyNs;
	
	@Parameter(property = "generateIdField", defaultValue = "false")
	private boolean generateIdField;
	@Parameter(property = "idFieldName", defaultValue = "id")
	private String idFieldName;
	
	@Component
	private BuildContext buildContext;
	@Component
	private MavenSession mavenSession;
	@Component
	private PluginDescriptor pluginDescriptor;

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final Path outputPath = output.toPath();
			Path outputDir = Files.createDirectories(outputPath);
			final Log log = getLog();
			log.info(String.format("Generating {} java classes from ontology ",	ontologyUri));
			
			Codegen codegen = new Codegen();
			
			// the java package to create the classes in
			codegen.setJavaPackageName(packageName);
			
			// Ontology loading parameters
			codegen.setOntologyIri(ontologyNs);
			codegen.setOntologyPhysicalIri(ontologyUri);
			
			// where to write the source to
			codegen.setJavaSourceFolder(outputDir.toFile());
			
			// will generate "indName" String fields with @OwlIndividualId annotation and implementations
			codegen.setGenerateIdField(generateIdField);
			codegen.setIdFieldName(idFieldName);
			
			// generate code
			codegen.genCode();
			//TODO move *Impl.java into impl package (requires to manage imports into impl classes)
		
		} catch (CodegenException | IOException e) {
			throw new MojoExecutionException("Could not generate Java classes", e);
		}
	}

//	private File fetchVocab(URL url, final String displayName,
//			final Vocabulary vocab) throws URISyntaxException, IOException {
//		final HttpClientBuilder clientBuilder = HttpClientBuilder.create()
//				.setUserAgent(
//						String.format("%s:%s/%s (%s) %s:%s/%s (%s)",
//								pluginDescriptor.getGroupId(),
//								pluginDescriptor.getArtifactId(),
//								pluginDescriptor.getVersion(),
//								pluginDescriptor.getName(),
//								project.getGroupId(), project.getArtifactId(),
//								project.getVersion(), project.getName()));
//		final Path cache = remoteCacheDir.toPath();
//		Files.createDirectories(cache);
//		try (CloseableHttpClient client = clientBuilder.build()) {
//			final HttpUriRequest request = RequestBuilder.get()
//					.setUri(url.toURI())
//					.setHeader(HttpHeaders.ACCEPT, getAcceptHeaderValue())
//					.build();
//			return client.execute(request, new ResponseHandler<File>() {
//				@Override
//				public File handleResponse(HttpResponse response)
//						throws IOException {
//					final Log log = getLog();
//					// Check the mime-type
//					String mime = mimeType;
//					if (vocab.getMimeType() != null) {
//						mime = vocab.getMimeType();
//					}
//					if (mime == null) {
//						mime = getHeaderValue(response,
//								HttpHeaders.CONTENT_TYPE);
//						log.debug("Using mime-type from response-header: "
//								+ mime);
//					}
//					final RDFFormat format = Rio
//							.getParserFormatForMIMEType(mime);
//					final String fName;
//					if (format == null) {
//						fName = displayName + ".cache";
//						log.debug(String.format(
//								"Unknown format, cache will be %s", fName));
//					} else {
//						fName = displayName + "."
//								+ format.getDefaultFileExtension();
//						log.debug(String.format("%s format, cache will be %s",
//								format.getName(), fName));
//					}
//					Path cacheFile = cache.resolve(fName);
//					if (Files.exists(cacheFile)) {
//						log.debug(String.format(
//								"Cache-File %s found, checking if up-to-date",
//								cacheFile));
//						// Check if the cache is up-to-date
//						final FileTime fileTime = Files
//								.getLastModifiedTime(cache);
//						final Date remoteDate = DateUtils
//								.parseDate(getHeaderValue(response,
//										HttpHeaders.LAST_MODIFIED));
//						if (remoteDate != null
//								&& remoteDate.getTime() < fileTime.toMillis()) {
//							// The remote file was changed before the cache, so
//							// no action required
//							log.debug(String
//									.format("%tF %<tT is after %tF %<tT, no action required",
//											new Date(fileTime.toMillis()),
//											remoteDate));
//							return null;
//						} else {
//							log.debug(String
//									.format("remote file is newer - need to rebuild vocabulary"));
//						}
//					} else {
//						log.debug(String.format(
//								"No Cache-File %s, need to fetch", cacheFile));
//					}
//					final File cf = cacheFile.toFile();
//					FileUtils.copyInputStreamToFile(response.getEntity()
//							.getContent(), cf);
//					log.info(String.format(
//							"Fetched vocabulary definition for %s from %s",
//							displayName, request.getURI()));
//					return cf;
//				}
//
//				private String getHeaderValue(HttpResponse response,
//						String header) {
//					final Header h = response.getFirstHeader(header);
//					if (h != null) {
//						return h.getValue();
//					} else {
//						return null;
//					}
//				}
//			});
//		}
//	}

}