/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.jayway.restassured.internal.filter

import com.jayway.restassured.filter.Filter
import com.jayway.restassured.filter.FilterContext
import com.jayway.restassured.internal.http.Method
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.FilterableRequestSpecification
import com.jayway.restassured.specification.FilterableResponseSpecification
import com.jayway.restassured.specification.RequestSender
import org.codehaus.groovy.runtime.ReflectionMethodInvoker

class FilterContextImpl implements FilterContext {
    def private Iterator<Filter> filters
    def private completePath;
    def private path;
    def private Method method;
    def assertionClosure
    def properties = [:]

    FilterContextImpl(String completePath, String path, Method method, assertionClosure, List<Filter> filterList) {
        this.filters = filterList.iterator()
        this.completePath = completePath
        this.path = path
        this.method = method
        this.assertionClosure = assertionClosure
    }

    Response next(FilterableRequestSpecification request, FilterableResponseSpecification response) {
        if (filters.hasNext()) {
            def next = filters.next();
            return next.filter(request, response, this)
        }
    }

    String getRequestPath() {
        path
    }

    Method getRequestMethod() {
        method
    }

    String getCompleteRequestPath() {
        return completePath
    }

    Response send(RequestSender requestSender) {
        return ReflectionMethodInvoker.invoke(requestSender, method.toString().toLowerCase(), path)
    }

    void setValue(String name, Object value) {
        properties.put(name, value)
    }

    boolean hasValue(String name) {
        return getValue(name) != null
    }

    def getValue(String name) {
        return properties.get(name)
    }
}
