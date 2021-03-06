/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jooby.internal.reqparam;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import org.jooby.ParamConverter;

import com.google.inject.TypeLiteral;

public class OptionalParamConverter implements ParamConverter {

  private boolean matches(final TypeLiteral<?> toType) {
    return Optional.class == toType.getRawType() && toType.getType() instanceof ParameterizedType;
  }

  @Override
  public Object convert(final TypeLiteral<?> toType, final Object[] values, final Context ctx)
      throws Exception {
    if (matches(toType)) {
      if (values == null) {
        return Optional.empty();
      }
      TypeLiteral<?> paramType = TypeLiteral.get(((ParameterizedType) toType.getType())
          .getActualTypeArguments()[0]);
      return Optional.of(ctx.convert(paramType, values));
    } else {
      return ctx.convert(toType, values);
    }
  }

}
