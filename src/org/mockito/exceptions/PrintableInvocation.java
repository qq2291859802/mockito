/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.exceptions;

import org.mockito.invocation.DescribedInvocation;
import org.mockito.invocation.Location;

@Deprecated
/**
 *
 * 使用DescribedInvocation替换
 *
 *
 * @Deprecated. We needed to move this class to a better place to keep consistency of the API.
 * Please use {@link DescribedInvocation} instead.
 */
public interface PrintableInvocation {

    String toString();

    Location getLocation();

}