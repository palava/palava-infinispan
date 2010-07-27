/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.infinispan;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * A special exception which may contain multiple exceptions.
 * 
 * @author Tobias Sarnowski
 */
public class InfinispanExceptions extends Exception {
    
    private static final Joiner JOINER = Joiner.on("\n\n");
    
    private static final long serialVersionUID = 2320381968292003743L;
    
    private final List<Exception> exceptions;

    public InfinispanExceptions(List<Exception> exceptions) {
        super(exceptions.get(0));
        this.exceptions = exceptions;
    }

    public InfinispanExceptions(String message, List<Exception> exceptions) {
        super(message, exceptions.get(0));
        this.exceptions = exceptions;
    }

    public List<Exception> getExceptions() {
        return ImmutableList.copyOf(exceptions);
    }

    @Override
    public String getMessage() {
        return getMessage() + "\n\n" + JOINER.join(exceptions);
    }
    
}
