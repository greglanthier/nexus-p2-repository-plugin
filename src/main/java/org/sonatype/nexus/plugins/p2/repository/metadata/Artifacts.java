/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.metadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.nexus.plugins.p2.repository.P2Constants;

public class Artifacts
    extends AbstractMetadata
{

    public static final String[][] PACKED_MAPPING_RULES = {
        { "(& (classifier=osgi.bundle) (format=packed))", "${repoUrl}/plugins/${id}_${version}.jar.pack.gz" }, //$NON-NLS-1$//$NON-NLS-2$
        { "(& (classifier=osgi.bundle))", "${repoUrl}/plugins/${id}_${version}.jar" }, //$NON-NLS-1$//$NON-NLS-2$
        { "(& (classifier=binary))", "${repoUrl}/binary/${id}_${version}" }, //$NON-NLS-1$ //$NON-NLS-2$
        { "(& (classifier=org.eclipse.update.feature))", "${repoUrl}/features/${id}_${version}.jar" } }; //$NON-NLS-1$//$NON-NLS-2$

    public static final String[][] DEFAULT_MAPPING_RULES = {
        { "(& (classifier=osgi.bundle))", "${repoUrl}/plugins/${id}_${version}.jar" }, //$NON-NLS-1$//$NON-NLS-2$
        { "(& (classifier=binary))", "${repoUrl}/binary/${id}_${version}" }, //$NON-NLS-1$ //$NON-NLS-2$
        { "(& (classifier=org.eclipse.update.feature))", "${repoUrl}/features/${id}_${version}.jar" } }; //$NON-NLS-1$//$NON-NLS-2$

    public Artifacts( final Xpp3Dom dom )
    {
        super( dom );
    }

    public Artifacts( final String name )
    {
        super( new Xpp3Dom( "repository" ) );
        setRepositoryAttributes( name );
    }

    public void setRepositoryAttributes( final String name )
    {
        getDom().setAttribute( "name", name );
        getDom().setAttribute( "type", "org.eclipse.equinox.p2.artifact.repository.simpleRepository" );
        getDom().setAttribute( "version", "1" );
    }

    public static class Artifact
        extends AbstractMetadata
    {

        protected Artifact( final Xpp3Dom dom )
        {
            super( dom );
        }

        public Artifact( final Artifact artifact )
        {
            super( artifact );
        }

        public String getClassifier()
        {
            return dom.getAttribute( "classifier" );
        }

        public String getId()
        {
            return dom.getAttribute( "id" );
        }

        public String getVersion()
        {
            return dom.getAttribute( "version" );
        }

        public String getFormat()
        {
            return getProperties().get( P2Constants.ARTIFACT_PROP_FORMAT );
        }
    }

    public List<Artifact> getArtifacts()
    {
        final Xpp3Dom artifactsDom = dom.getChild( "artifacts" );

        return getArtifacts( artifactsDom );
    }

    public static List<Artifact> getArtifacts( final Xpp3Dom artifactsDom )
    {
        final List<Artifact> result = new ArrayList<Artifact>();

        if ( artifactsDom != null )
        {
            for ( final Xpp3Dom artifactDom : artifactsDom.getChildren( "artifact" ) )
            {
                result.add( new Artifact( artifactDom ) );
            }
        }

        return result;
    }

    public void setArtifacts( final List<Artifact> artifacts )
    {
        removeChild( dom, "artifacts" );
        final Xpp3Dom artifactsDom = new Xpp3Dom( "artifacts" );

        for ( final Artifact artifact : artifacts )
        {
            artifactsDom.addChild( artifact.getDom() );
        }
        artifactsDom.setAttribute( "size", Integer.toString( artifacts.size() ) );

        dom.addChild( artifactsDom );
    }

    public void setMapping( final String[][] mappings )
    {
        removeChild( dom, "mappings" );
        final Xpp3Dom mappingsDom = new Xpp3Dom( "mappings" );

        for ( final String[] rule : mappings )
        {
            final Xpp3Dom ruleDom = new Xpp3Dom( "rule" );
            ruleDom.setAttribute( "filter", rule[0] );
            ruleDom.setAttribute( "output", rule[1] );

            mappingsDom.addChild( ruleDom );
        }
        mappingsDom.setAttribute( "size", Integer.toString( mappings.length ) );

        dom.addChild( mappingsDom );
    }

    public Map<String, String> getMappings()
    {
        final Map<String, String> result = new LinkedHashMap<String, String>();

        final Xpp3Dom mappingsDom = dom.getChild( "mappings" );

        if ( mappingsDom != null )
        {
            for ( final Xpp3Dom ruleDom : mappingsDom.getChildren( "rule" ) )
            {
                result.put( ruleDom.getAttribute( "filter" ), ruleDom.getAttribute( "output" ) );
            }
        }

        return result;
    }
}
