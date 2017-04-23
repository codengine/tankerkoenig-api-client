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

public class CorrectionRequestTest
{
   @Test
   public void isFinal()
   {
      assertThat(CorrectionRequest.class).isFinal();
   }

   @Test
   public void checkCompleteConstruction() throws RequesterException
   {
      final Requester requester = mock(Requester.class);
      final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, CorrectionRequest.Type.WRONG_PRICE_E5);
      assertThat(request.getEndpoint()).isEqualTo("complaint.php");
      assertThat(request.getMethod()).isEqualTo(Request.Method.POST);
      assertThat(request.getResultClass()).isEqualTo(CorrectionResult.class);
      assertThat(request.getApiKey()).isEqualTo("123");
      assertThat(request.getBaseUrl()).isEqualTo("http://test/");

      request.setStationId("MyStation");
      request.setCorrectionValue("1.23");

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("type", "wrongPriceE5")
            .with("id", "MyStation")
            .with("correction", "1.23");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.execute();

      verify(requester, times(1)).execute(request, request.getResultClass());
   }

   @Test
   public void checkAllTypes()
   {
      final Requester requester = mock(Requester.class);

      for (final CorrectionRequest.Type type : CorrectionRequest.Type.values())
      {
         final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, type);

         Map<String, Object> expectedParams = new FluentMap<String, Object>()
               .with("type", type.toQueryParam())
               .with("id", null);

         if (type.requiresCorrectionValue())
         {
            request.setCorrectionValue("fixme");
            expectedParams.put("correction", "fixme");
         }

         assertParamValues(request.getRequestParameters(), expectedParams);
      }
   }

   @Test
   public void validateTypeIsNull()
   {
      final Requester requester = mock(Requester.class);
      final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, null);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessage("Type must not be null");
   }

   @Test
   public void validateStationIdNotNullNorEmpty()
   {
      final Requester requester = mock(Requester.class);
      final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, CorrectionRequest.Type.WRONG_PRICE_E5);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessage("Station ID must not be null");

      request.setStationId("");

      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessage("Station ID must not be empty");
   }

   @Test
   public void validateCorrectionValueIsPresentWhenRequired()
   {
      final Requester requester = mock(Requester.class);
      final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, CorrectionRequest.Type.WRONG_PRICE_E5);

      request.setStationId("123");

      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessage("Correction Value must not be null");

      request.setCorrectionValue("");

      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessage("Correction Value must not be empty");
   }

   @Test
   public void validateTypes()
   {
      final Requester requester = mock(Requester.class);

      for (final CorrectionRequest.Type type : CorrectionRequest.Type.values())
      {
         assertThat(type.toQueryParam()).isNotNull().isNotEmpty();

         if (!type.requiresCorrectionValue())
         {
            continue;
         }

         final CorrectionRequest request = new CorrectionRequest("123", "http://test/", requester, type);
         request.setStationId("123");

         switch (type)
         {
            case WRONG_PRICE_E5:
            case WRONG_PRICE_E10:
            case WRONG_PRICE_DIESEL:
               request.setCorrectionValue("foo");
               assertThatThrownBy(request::validate).hasMessageEndingWith("is not a valid floating point value for Correction Value");
               request.setCorrectionValue("0.12");
               request.validate();
               break;
            case WRONG_PETROL_STATION_POSTCODE:
               request.setCorrectionValue("foo");
               assertThatThrownBy(request::validate).hasMessageContaining("is not a valid post code");
               request.setCorrectionValue("55555");
               request.validate();
               break;
            case WRONG_PETROL_STATION_LOCATION:
               request.setCorrectionValue("foo");
               assertThatThrownBy(request::validate).hasMessageContaining("is not a location data");
               request.setCorrectionValue("55.2,33.2");
               request.validate();
               break;
         }
      }
   }

   @Test
   public void requiresCorrectionValue()
   {
      for (final CorrectionRequest.Type type : CorrectionRequest.Type.values())
      {
         switch (type)
         {
            case WRONG_STATUS_OPEN:
            case WRONG_STATUS_CLOSED:
               assertThat(type.requiresCorrectionValue()).isFalse();
               break;
            default:
               assertThat(type.requiresCorrectionValue()).isTrue();
               break;
         }
      }
   }
}