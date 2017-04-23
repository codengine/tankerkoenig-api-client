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

package de.codengine.tankerkoenig.models.requests;

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertParamValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.stream.IntStream;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.codengine.tankerkoenig.exception.RequesterException;
import de.codengine.tankerkoenig.utils.FluentMap;

public class PricesRequestTest
{
   private Requester requester;
   private PricesRequest request;

   @Before
   public void setUp() throws Exception
   {
      requester = mock(Requester.class);
      request = new PricesRequest("123", "http://test/", requester);
   }

   @After
   public void tearDown() throws Exception
   {
      requester = null;
      request = null;
   }

   @Test
   public void isFinal()
   {
      assertThat(PricesRequest.class).isFinal();
   }

   @Test
   public void checkCompleteConstruction()
   {
      assertThat(request.getEndpoint()).isEqualTo("prices.php");
      assertThat(request.getMethod()).isEqualTo(Request.Method.GET);
      assertThat(request.getResultClass()).isEqualTo(PricesResult.class);
      assertThat(request.getApiKey()).isEqualTo("123");
      assertThat(request.getBaseUrl()).isEqualTo("http://test/");
   }

   @Test
   public void addId()
   {
      request.addId("1");

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("ids", "1");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.addId("2");
      request.addId("2");
      request.addId(null);
      request.addId("");

      expectedParams = new FluentMap<String, Object>()
            .with("ids", "1,2");

      assertParamValues(request.getRequestParameters(), expectedParams);
      request.validate();
   }

   @Test
   public void addIds()
   {
      request.addIds("1");

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("ids", "1");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.addIds("2", "2", null, "");

      expectedParams = new FluentMap<String, Object>()
            .with("ids", "1,2");

      assertParamValues(request.getRequestParameters(), expectedParams);
      request.validate();
   }

   @Test
   public void addIdsWithCollection()
   {
      request.addIds(Lists.emptyList());

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("ids", "");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.addIds(Lists.newArrayList("2", "2", null, "", "1"));

      expectedParams = new FluentMap<String, Object>()
            .with("ids", "2,1");

      assertParamValues(request.getRequestParameters(), expectedParams);
      request.validate();
   }

   @Test
   public void validate()
   {
      assertThatThrownBy(request::validate).hasMessage("IDs must not be empty");
      IntStream.range(1, 11)
            .forEach(value -> request.addId(String.valueOf(value)));
      request.validate();
      request.addId("11");
      assertThatThrownBy(request::validate).hasMessage("A maximum of 10 IDs is allowed per request");
   }

   @Test
   public void execute() throws RequesterException
   {
      IntStream.range(1, 11).forEach(value -> request.addId(String.valueOf(value)));
      request.execute();
      verify(requester, times(1)).execute(request, request.getResultClass());
   }
}