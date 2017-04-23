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

import java.util.Collection;
import java.util.regex.Pattern;

import de.codengine.tankerkoenig.exception.RequestParamException;

final class RequestParamValidator
{
   private final static Pattern postCodePattern;
   private final static Pattern floatPattern;
   private final static Pattern locationPattern;

   static
   {
      postCodePattern = Pattern.compile("\\d{5}");
      floatPattern = Pattern.compile("[-]?[0-9]*\\.[0-9]+");
      locationPattern = Pattern.compile(floatPattern.pattern() + "\\s?,\\s?" + floatPattern.pattern());
   }

   private RequestParamValidator()
   {
      throw new UnsupportedOperationException();
   }

   static void minMax(final int value, final int min, final int max, final String label)
   {
      if (value < min || value > max)
      {
         throw new RequestParamException("%s has to be between %s and %s", label, min, max);
      }
   }

   static void minMax(final double value, final int min, final int max, final String label)
   {
      if (value < min || value > max)
      {
         throw new RequestParamException("%s has to be between %s and %s", label, min, max);
      }
   }

   static void notNull(final Object value, final String label)
   {
      if (value == null)
      {
         throw new RequestParamException("%s must not be null", label);
      }
   }

   static void notEmpty(final String value, final String label)
   {
      notNull(value, label);
      if (value.isEmpty())
      {
         throw new RequestParamException("%s must not be empty", label);
      }
   }

   static void notEmpty(final Collection<?> collection, final String label)
   {
      notNull(collection, label);
      if (collection.isEmpty())
      {
         throw new RequestParamException("%s must not be empty", label);
      }
   }

   static void maxCount(final Collection<String> collection, final int max, final String label)
   {
      if (max < 0)
      {
         throw new RuntimeException("Max must not be below 0");
      }

      notNull(collection, label);

      if (collection.size() > max)
      {
         throw new RequestParamException("A maximum of %s %s is allowed per request", max, label);
      }
   }

   static void isFloat(final String value, final String label)
   {
      notNull(value, label);
      if (!floatPattern.matcher(value).matches())
      {
         throw new RequestParamException("%s is not a valid floating point value for %s", value, label);
      }
   }

   static void isPostCode(final String value)
   {
      notNull(value, "Post Code");
      if (value.length() != 5 || !postCodePattern.matcher(value).matches())
      {
         throw new RequestParamException("%s is not a valid post code, it must have a format of 12345", value);
      }
   }

   static void isLocationData(final String value)
   {
      notNull(value, "Location");
      if (value.length() < 3 || !locationPattern.matcher(value).matches())
      {
         throw new RequestParamException("%s is not a location data, it must be in the format of \"58.0,13.0\"", value);
      }
   }
}
