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

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.codengine.tankerkoenig.exception.RequestParamException;
import de.codengine.tankerkoenig.utils.FluentMap;

public class StationListRequestTest
{
   private Requester requester;
   private StationListRequest request;

   @Before
   public void setUp() throws Exception
   {
      requester = mock(Requester.class);
      request = new StationListRequest("123", "http://test/", requester);
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
      assertThat(StationListRequest.class).isFinal();
   }

   @Test
   public void checkCompleteConstruction()
   {
      assertThat(request.getEndpoint()).isEqualTo("list.php");
      assertThat(request.getMethod()).isEqualTo(Request.Method.GET);
      assertThat(request.getResultClass()).isEqualTo(StationListResult.class);
      assertThat(request.getApiKey()).isEqualTo("123");
      assertThat(request.getBaseUrl()).isEqualTo("http://test/");
   }

   @Test
   public void checkDefaults()
   {
      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("lat", null)
            .with("lng", null)
            .with("rad", 5.0)
            .with("type", "all")
            .with("sort", "dist");

      assertParamValues(request.getRequestParameters(), expectedParams);
   }

   @Test
   public void checkSetter()
   {
      request.setCoordinates(55.1, 12.2);
      request.setGasRequestType(GasRequestType.ALL);
      request.setSorting(StationListRequest.SortingRequestType.PRICE);
      request.setSearchRadius(22);

      Map<String, Object> expectedParams = new FluentMap<String, Object>()
            .with("lat", 55.1)
            .with("lng", 12.2)
            .with("rad", 22.0)
            .with("type", "all")
            .with("sort", "dist");

      assertParamValues(request.getRequestParameters(), expectedParams);

      request.setGasRequestType(GasRequestType.E5);
      request.setSorting(StationListRequest.SortingRequestType.PRICE);

      expectedParams = new FluentMap<String, Object>()
            .with("lat", 55.1)
            .with("lng", 12.2)
            .with("rad", 22.0)
            .with("type", "e5")
            .with("sort", "price");

      assertParamValues(request.getRequestParameters(), expectedParams);
   }

   @Test
   public void validate()
   {
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class)
            .hasMessageEndingWith("must not be null");
      request.setCoordinates(-91, 5);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setCoordinates(91, 5);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setCoordinates(-90, 5);
      request.validate();
      request.setCoordinates(90, 5);
      request.validate();

      request.setCoordinates(53, -181);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setCoordinates(53, 181);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setCoordinates(53, -180);
      request.validate();
      request.setCoordinates(53, 180);
      request.validate();

      request.setSearchRadius(0);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setSearchRadius(26);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setSearchRadius(1);
      request.validate();
      request.setSearchRadius(25);
      request.validate();

      request.setGasRequestType(null);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setGasRequestType(GasRequestType.ALL);
      request.validate();

      request.setSorting(null);
      assertThatThrownBy(request::validate).isExactlyInstanceOf(RequestParamException.class);
      request.setSorting(StationListRequest.SortingRequestType.PRICE);
      request.validate();
   }
}