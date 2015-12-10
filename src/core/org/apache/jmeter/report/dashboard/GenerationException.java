/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter.report.dashboard;

/**
 * The class GenerationException provides an exception when report generation
 * fails.
 *
 * @since 2.14
 */
public class GenerationException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8344451600520488094L;

    /**
     * Instantiates a new configuration exception.
     */
    public GenerationException() {
        super();
    }

    /**
     * Instantiates a new configuration exception.
     *
     * @param message
     *            the message
     */
    public GenerationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new configuration exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
