/**
 * Copyright (C) 2010 hprange <hprange@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.resurgences.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class Interceptor {

    protected static final Logger log = LoggerFactory.getLogger(Interceptor.class);

    private Injector injector;

    public Object injectMembers(Object object) {
        if (object == null) {
            return null;
        }
        Injector currentInjector = getInjector();
        if (currentInjector != null) {
            currentInjector.injectMembers(object);
        } else {
            Class<? extends Object> clazz = object.getClass();
            if (WOInject.isWonderClass(clazz)) {
                throw new IllegalStateException("Class '" + clazz + "' instanciated before guice init.");
            } else {
                log.info("Class '" + clazz
                        + "' instanciated before guice init, but this class shouldn't be injected anyway");
            }
        }

        return object;
    }

    private Injector getInjector() {
        if (injector == null) {
            injector = InjectorLocator.getInjector();
        }
        return injector;
    }
}
