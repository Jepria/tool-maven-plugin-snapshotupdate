package org.jepria.tools.mavenplugin.snapshotupdate;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.BuildPluginManager;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

//import org.apache.commons.io.FilenameUtils;

import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Goal which updates snapshot.
 *
 */
@Mojo( name = "update-snapshot", defaultPhase = LifecyclePhase.VALIDATE, requiresDependencyResolution = ResolutionScope.TEST )
public class SnapshotUpdateMojo
    extends AbstractMojo
{

  /**
   * Plugin configuration to use in the execution.
   */
  @Parameter
  private XmlPlexusConfiguration configuration;
  
  /**
   * Snapshot location.
   */
  @Parameter( property = "file", required = true )
  private File file;
  
  /**
   * Snapshot group id.
   */
  @Parameter( property = "groupId", required = true )
  private String groupId;
  
  /**
   * Snapshot artifact id.
   */
  @Parameter( property = "artifactId", required = true )
  private String artifactId;

  /**
   * Snapshot version.
   */
  @Parameter( property = "version", required = true )
  private String version;
  
  /**
   * Snapshot packaging.
   */
  @Parameter( property = "packaging" )
  private String packaging;
  
  /**
   * The project currently being build.
   */
  @Parameter( defaultValue = "${project}", readonly = true )
  private MavenProject mavenProject;

  /**
   * The current Maven session.
   */
  @Parameter( defaultValue = "${session}", readonly = true )
  private MavenSession mavenSession;

  /**
   * The Maven BuildPluginManager component.
   */
  @Component
  private BuildPluginManager pluginManager;
    
    public void execute()
        throws MojoExecutionException
    {

      if (file.exists() && file.isFile() && version.toUpperCase().endsWith("-SNAPSHOT")){
        executeMojo(
          plugin(
            groupId("org.apache.maven.plugins"),
            artifactId("maven-install-plugin"),
            version("2.5.2")
          ),
          goal("install-file"),
          configuration(
            element(name("file"), file.getPath()),
            element(name("repositoryLayout"), "default"),
            element(name("groupId"), groupId),
            element(name("artifactId"), artifactId),
            element(name("version"), version),
            element(name("packaging"), packaging != null ? packaging : "jar"),
            element(name("generatePom"), "true")
          ),
          executionEnvironment(
            mavenProject,
            mavenSession,
            pluginManager
          )
        );          
      } else{
        getLog().info("Non-SNAPSHOT version");
      }
      
    }
    
}
