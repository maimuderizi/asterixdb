/*
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
package org.apache.hyracks.storage.am.common.api;

import org.apache.hyracks.api.exceptions.HyracksDataException;
import org.apache.hyracks.dataflow.common.data.accessors.ITupleReference;

public interface IIndexBulkLoader {
    /**
     * Append a tuple to the index in the context of a bulk load.
     *
     * @param tuple
     *            Tuple to be inserted.
     * @throws IndexException
     *             If the input stream is invalid for bulk loading (e.g., is not sorted).
     * @throws HyracksDataException
     *             If the BufferCache throws while un/pinning or un/latching.
     */
    public void add(ITupleReference tuple) throws IndexException, HyracksDataException;

    /**
     * Finalize the bulk loading operation in the given context.
     *
     * @throws IndexException
     * @throws HyracksDataException
     *             If the BufferCache throws while un/pinning or un/latching.
     */
    public void end() throws IndexException, HyracksDataException;

    /**
     * Release all resources held by this bulkloader, with no guarantee of
     * persisted content.
     */
    void abort() throws HyracksDataException;
}
