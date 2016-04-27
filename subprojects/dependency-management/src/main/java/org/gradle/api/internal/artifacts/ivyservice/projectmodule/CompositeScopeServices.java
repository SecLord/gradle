/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.artifacts.ivyservice.projectmodule;

import org.gradle.StartParameter;
import org.gradle.api.internal.artifacts.ResolveContext;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ComponentResolvers;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.DelegatingComponentResolvers;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ResolverProviderFactory;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.internal.service.ServiceRegistry;

public class CompositeScopeServices {
    private final StartParameter startParameter;
    private final ServiceRegistry compositeServices;

    public CompositeScopeServices(StartParameter startParameter, ServiceRegistry compositeServices) {
        this.startParameter = startParameter;
        this.compositeServices = compositeServices;
    }

    ProjectArtifactBuilder createCompositeProjectArtifactBuilder(CompositeProjectComponentRegistry projectComponentRegistry, GradleLauncherFactory  gradleLauncherFactory) {
        return new DefaultProjectArtifactBuilder(projectComponentRegistry, gradleLauncherFactory, startParameter, compositeServices);
    }

    ResolverProviderFactory createCompositeResolverProviderFactory(CompositeProjectComponentRegistry projectComponentRegistry, ProjectArtifactBuilder artifactBuilder) {
        return new CompositeProjectResolverProviderFactory(projectComponentRegistry, artifactBuilder);
    }

    private static class CompositeProjectResolverProviderFactory implements ResolverProviderFactory {
        private final CompositeProjectDependencyResolver resolver;

        public CompositeProjectResolverProviderFactory(CompositeProjectComponentRegistry projectComponentRegistry, ProjectArtifactBuilder artifactBuilder) {
            resolver = new CompositeProjectDependencyResolver(projectComponentRegistry, artifactBuilder);
        }

        @Override
        public boolean canCreate(ResolveContext context) {
            return true;
        }

        @Override
        public ComponentResolvers create(ResolveContext context) {
            return DelegatingComponentResolvers.of(resolver);
        }
    }

}
