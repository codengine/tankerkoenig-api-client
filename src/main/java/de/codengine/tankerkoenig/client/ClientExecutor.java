/*
 * MIT License
 *
 * Copyright (c) 2017 Stefan Hueg (Codengine)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.codengine.tankerkoenig.client;

import java.util.Map;

import de.codengine.tankerkoenig.exception.ClientExecutorException;
import de.codengine.tankerkoenig.models.requests.Request;
import de.codengine.tankerkoenig.models.requests.Requester;

/**
 * Executes requests that are built from the {@link Requester} and originate
 * in the {@link Request}
 */
public interface ClientExecutor
{
   /**
    * Executes a GET request
    *
    * @param url             The request URL
    * @param queryParameters The query parameters
    * @return The response body
    * @throws ClientExecutorException Should be thrown if any parameter or client-side error occurs
    */
   String get(final String url, final Map<String, Object> queryParameters) throws ClientExecutorException;

   /**
    * Executes a POST request. Request Parameters should be sent as forms (not multipart)
    *
    * @param url        The request URL
    * @param formParams The form parameters
    * @return The response body
    * @throws ClientExecutorException Should be thrown if any parameter or client-side error occurs
    */
   String post(final String url, final Map<String, Object> formParams) throws ClientExecutorException;
}
