/*
 *    Copyright 2021 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package te4j.template.compiler.path;

import te4j.util.type.GenericInfo;
import te4j.util.type.NullTypeInfo;
import te4j.util.type.TypeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author whilein
 */
public final class PathAccessor {

    public static final PathAccessor TRUE = new PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "true");
    public static final PathAccessor FALSE = new PathAccessor(GenericInfo.PRIMITIVE_BOOLEAN, "false");
    public static final PathAccessor NULL = new PathAccessor(NullTypeInfo.INSTANCE, "null");

    private final TypeInfo returnType;
    private final String accessor;

    public PathAccessor(TypeInfo returnType, String accessor) {
        this.returnType = returnType;
        this.accessor = accessor;
    }

    public PathAccessor(Type type, String acccessor) {
        this(new GenericInfo(type, new Annotation[0]), acccessor);
    }

    public TypeInfo getReturnType() {
        return returnType;
    }

    public String getAccessor() {
        return accessor;
    }

}
