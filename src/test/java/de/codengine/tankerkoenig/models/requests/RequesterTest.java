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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.codengine.tankerkoenig.client.ClientExecutor;
import de.codengine.tankerkoenig.exception.ClientExecutorException;
import de.codengine.tankerkoenig.exception.RequestParamException;
import de.codengine.tankerkoenig.exception.RequesterException;
import de.codengine.tankerkoenig.models.mapper.JsonMapper;
import de.codengine.tankerkoenig.utils.FluentMap;

public class RequesterTest
{
   private JsonMapper jsonMapper;
   private ClientExecutor clientExecutor;
   private Requester requester;

   @Before
   public void setUp() throws Exception
   {
      jsonMapper = mock(JsonMapper.class);
      clientExecutor = mock(ClientExecutor.class);
      requester = new Requester(clientExecutor, jsonMapper);
   }

   @After
   public void tearDown() throws Exception
   {
      jsonMapper = null;
      clientExecutor = null;
      requester = null;
   }

   @Test
   public void notFinal()
   {
      assertThat(Requester.class).isNotFinal();
   }

   @Test
   public void exceptionThrownByClientExecutorIsCaught() throws RequesterException
   {
      when(clientExecutor.get(any(), any())).thenThrow(ClientExecutorException.class);
      final RequestStub request = new RequestStub("123", "http://test/", requester);
      assertThatThrownBy(() -> requester.execute(request, ResultStub.class))
            .isExactlyInstanceOf(RequesterException.class)
            .hasCauseExactlyInstanceOf(ClientExecutorException.class);
   }

   @Test
   public void exceptionThrownByValidationIsCaught() throws RequesterException
   {
      final RequestStub request = mock(RequestStub.class);
      doThrow(RequestParamException.class).when(request).validate();

      assertThatThrownBy(() -> requester.execute(request, ResultStub.class))
            .isExactlyInstanceOf(RequesterException.class)
            .hasCauseExactlyInstanceOf(RequestParamException.class);
   }

   @Test
   public void executeGetRequest() throws RequesterException
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("apikey", "shouldnotbehere")
            .with("id", "5");

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.get(any(), any())).thenReturn("Result");
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      final ResultStub result = requester.execute(request, ResultStub.class);
      assertThat(result).isSameAs(resultStub);

      verify(request, times(1))
            .getRequestParameters();

      verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("http://test/stub.php")), argThat(
            argument -> Objects.equals(argument.get("id"), "5") && argument.containsKey("ts") && Objects.equals(argument.get("apikey"), "123")
      ));

      verify(jsonMapper).fromJson(argThat(argument -> argument.equals("Result")), argThat(argument -> argument.equals(ResultStub.class)));
   }

   @Test
   public void executePostRequest() throws RequesterException
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester).setMethod(Request.Method.POST));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("apikey", "shouldnotbehere")
            .with("id", "5");

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.post(any(), any())).thenReturn("Result");
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      final ResultStub result = requester.execute(request, ResultStub.class);
      assertThat(result).isSameAs(resultStub);

      verify(request, times(1))
            .getRequestParameters();

      verify(clientExecutor, times(1)).post(argThat(argument -> argument.equals("http://test/stub.php")), argThat(
            argument -> Objects.equals(argument.get("id"), "5") && argument.containsKey("ts") && Objects.equals(argument.get("apikey"), "123")
      ));

      verify(jsonMapper).fromJson(argThat(argument -> argument.equals("Result")), argThat(argument -> argument.equals(ResultStub.class)));
   }

   @Test
   public void executeGetSetsTS() throws RequesterException
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("id", "5")
            .with("ts", 1234567890);

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.get(any(), any())).thenReturn("Result");
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      final ResultStub result = requester.execute(request, ResultStub.class);
      assertThat(result).isSameAs(resultStub);

      verify(clientExecutor, times(1)).get(argThat(argument -> argument.equals("http://test/stub.php")), argThat(
            argument -> Objects.equals(argument.get("ts"), 1234567890)
      ));
   }

   @Test
   public void executePostRequestSetsTS() throws RequesterException
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester).setMethod(Request.Method.POST));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("id", "5")
            .with("ts", 1234567890);

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.post(any(), any())).thenReturn("Result");
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      final ResultStub result = requester.execute(request, ResultStub.class);
      assertThat(result).isSameAs(resultStub);

      verify(clientExecutor, times(1)).post(argThat(argument -> argument.equals("http://test/stub.php")), argThat(
            argument -> Objects.equals(argument.get("ts"), 1234567890)
      ));
   }

   @Test
   public void catchesAndWrapsClientExecutorException()
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester).setMethod(Request.Method.GET));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("id", "5");

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.get(any(), any())).thenThrow(ClientExecutorException.class);
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      assertThatThrownBy(() -> requester.execute(request, ResultStub.class))
            .isExactlyInstanceOf(RequesterException.class)
            .hasCauseExactlyInstanceOf(ClientExecutorException.class)
            .hasMessage("An exception was thrown while request execution");
   }

   @Test
   public void catchesAndWrapsGenericException()
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester).setMethod(Request.Method.GET));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("id", "5");

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.get(any(), any())).thenThrow(IllegalStateException.class);
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      assertThatThrownBy(() -> requester.execute(request, ResultStub.class))
            .isExactlyInstanceOf(RequesterException.class)
            .hasCauseExactlyInstanceOf(IllegalStateException.class)
            .hasMessage("An unhandled exception was thrown");
   }

   @Test
   public void doesNotCatchThrowables()
   {
      final RequestStub request = spy(new RequestStub("123", "http://test/", requester).setMethod(Request.Method.GET));
      ResultStub resultStub = new ResultStub();

      FluentMap<String, Object> paramMap = new FluentMap<String, Object>()
            .with("id", "5");

      when(request.getRequestParameters()).thenReturn(paramMap);
      when(clientExecutor.get(any(), any())).thenThrow(Throwable.class);
      when(jsonMapper.fromJson(anyString(), any())).thenReturn(resultStub);

      assertThatThrownBy(() -> requester.execute(request, ResultStub.class))
            .isExactlyInstanceOf(Throwable.class)
            .hasNoCause();
   }
}