/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap.classloader;

import com.navercorp.pinpoint.common.util.CodeSourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;

/**
 * @author emeroad
 */
public class PinpointClassLoaderTest {

    private final Class<?> slf4jClass = org.slf4j.LoggerFactory.class;

    @Test
    public void testOnLoadClass() throws Exception {
        ClassLoader classLoader = onLoadTest(ParallelClassLoader.class, slf4jClass);

        ClassLoaderUtils.close(classLoader);
    }

    /**
     * TODO duplicate code
     */
    private ClassLoader onLoadTest(Class<?> classLoaderType, Class<?> testClass) throws ClassNotFoundException {
        URL testClassJar = CodeSourceUtils.getCodeLocation(testClass);
        URL[] urls = {testClassJar};
        ClassLoader cl = PinpointClassLoaderFactory.createClassLoader(this.getClass().getName(), urls, null, ProfilerLibs.PINPOINT_PROFILER_CLASS);
        Assertions.assertSame(cl.getClass(), classLoaderType);

        try {
            cl.loadClass("test");
            Assertions.fail();
        } catch (ClassNotFoundException ignored) {
        }

        Class<?> selfLoadClass = cl.loadClass(testClass.getName());
        Assertions.assertNotSame(testClass, selfLoadClass);
        Assertions.assertSame(cl, selfLoadClass.getClassLoader());
        Assertions.assertSame(testClass.getClassLoader(), this.getClass().getClassLoader());
        return cl;
    }

}
