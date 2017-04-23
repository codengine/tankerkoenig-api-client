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

import org.junit.Test;

import de.codengine.tankerkoenig.exception.RequestParamException;
import de.codengine.tankerkoenig.exception.RequesterException;
import de.codengine.tankerkoenig.utils.FluentMap;

public class StationDetailRequestTest
{
   @Test
   public void isFinal()
   {
      assertThat(StationDetailRequest.class).isFinal();
   }

   @Test
   public void checkCompleteConstruction() throws RequesterException
   {
      final Requester requester = mock(Requester.class);
      final StationDetailRequest request = new StationDetailRequest("123", "http://test/", requester, "foo");

      assertThat(request.getEndpoint()).isEqualTo("detail.php");
      assertThat(request.getMethod()).isEqualTo(Request.Method.GET);
      assertThat(request.getResultClass()).isEqualTo(StationDetailResult.class);
      assertThat(request.getApiKey()).isEqualTo("123");
      assertThat(request.getBaseUrl()).isEqualTo("http://test/");

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("id", "foo");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.execute();

      verify(requester, times(1)).execute(request, request.getResultClass());
   }

   @Test
   public void validateWithEmptyId()
   {
      final Requester requester = mock(Requester.class);
      final StationDetailRequest request = new StationDetailRequest("123", "http://test/", requester, "");
      assertThatThrownBy(request::validate)
            .isExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void validateWithNullId()
   {
      final Requester requester = mock(Requester.class);
      final StationDetailRequest request = new StationDetailRequest("123", "http://test/", requester, null);
      assertThatThrownBy(request::validate)
            .isExactlyInstanceOf(RequestParamException.class);
   }
}