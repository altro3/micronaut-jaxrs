/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.jaxrs.container;

import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.jaxrs.common.JaxRsMutableResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;

/**
 * Handles JAX-RS exceptions that occur during the execution of an HTTP request.
 *
 * @author Denis Stepanov
 * @since 4.6.0
 */
@Singleton
@Produces
@Internal
final class JaxRsGlobalExceptionHandler implements ExceptionHandler<Throwable, HttpResponse<?>> {
    private final ErrorResponseProcessor<?> responseProcessor;
    private final Providers providers;

    /**
     * Constructor.
     *
     * @param responseProcessor Error Response Processor
     * @param providers         The providers
     */
    @Inject
    JaxRsGlobalExceptionHandler(ErrorResponseProcessor<?> responseProcessor, Providers providers) {
        this.responseProcessor = responseProcessor;
        this.providers = providers;
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, Throwable exception) {
        ExceptionMapper exceptionMapper = providers.getExceptionMapper(exception.getClass());
        if (exceptionMapper != null) {
            return ((JaxRsMutableResponse) exceptionMapper.toResponse(exception)).getResponse();
        }
        return responseProcessor.processResponse(ErrorContext.builder(request)
            .errorMessage(exception.getMessage())
            .cause(exception)
            .build(), HttpResponse.badRequest());
    }
}
