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

package de.codengine.tankerkoenig.models.mapper;

import static de.codengine.tankerkoenig.utils.CustomAsserts.assertDefaultFailedResponseAsserts;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import de.codengine.tankerkoenig.models.requests.CorrectionResult;
import de.codengine.tankerkoenig.utils.ResourceLoader;

public class CorrectionJsonMappingTest extends MapperTest
{
   @Test
   public void isFinal()
   {
      assertThat(CorrectionResult.class).isFinal();
   }

   @Test
   public void correction() throws IOException
   {
      final String complaintContent = ResourceLoader.readString("/correction.json");
      final CorrectionResult result = getMapper().fromJson(complaintContent, CorrectionResult.class);
      assertThat(result.getStatus()).isNotPresent();
      assertThat(result.getMessage()).isNotPresent();
      assertThat(result.getLicense()).isNotPresent();
      assertThat(result.getData()).isNotPresent();
      assertThat(result.isOk()).isTrue();
   }

   @Test
   public void withError() throws IOException
   {
      final String complaintContent = ResourceLoader.readString("/fail_response.json");
      final CorrectionResult result = getMapper().fromJson(complaintContent, CorrectionResult.class);
      assertDefaultFailedResponseAsserts(result);
   }
}
