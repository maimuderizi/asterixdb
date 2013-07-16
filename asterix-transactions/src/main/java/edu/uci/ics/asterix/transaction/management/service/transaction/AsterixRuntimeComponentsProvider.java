/*
 * Copyright 2009-2013 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.asterix.transaction.management.service.transaction;

import edu.uci.ics.asterix.common.api.IAsterixAppRuntimeContext;
import edu.uci.ics.asterix.common.context.BaseOperationTracker;
import edu.uci.ics.asterix.common.ioopcallbacks.LSMBTreeIOOperationCallbackFactory;
import edu.uci.ics.asterix.common.ioopcallbacks.LSMInvertedIndexIOOperationCallbackFactory;
import edu.uci.ics.asterix.common.ioopcallbacks.LSMRTreeIOOperationCallbackFactory;
import edu.uci.ics.hyracks.api.context.IHyracksTaskContext;
import edu.uci.ics.hyracks.storage.am.common.api.IIndexLifecycleManager;
import edu.uci.ics.hyracks.storage.am.common.api.IIndexLifecycleManagerProvider;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIOOperationCallback;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIOOperationCallbackFactory;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIOOperationCallbackProvider;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIOOperationScheduler;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIOOperationSchedulerProvider;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMIndex;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMMergePolicy;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMMergePolicyProvider;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMOperationTracker;
import edu.uci.ics.hyracks.storage.am.lsm.common.api.ILSMOperationTrackerProvider;
import edu.uci.ics.hyracks.storage.common.IStorageManagerInterface;
import edu.uci.ics.hyracks.storage.common.buffercache.IBufferCache;
import edu.uci.ics.hyracks.storage.common.file.IFileMapProvider;
import edu.uci.ics.hyracks.storage.common.file.ILocalResourceRepository;
import edu.uci.ics.hyracks.storage.common.file.ResourceIdFactory;

public class AsterixRuntimeComponentsProvider implements IIndexLifecycleManagerProvider, IStorageManagerInterface,
        ILSMIOOperationSchedulerProvider, ILSMMergePolicyProvider, ILSMOperationTrackerProvider,
        ILSMIOOperationCallbackProvider {
    private static final long serialVersionUID = 1L;

    private final ILSMIOOperationCallbackFactory ioOpCallbackFactory;
    private final boolean isSecondary;

    public static final AsterixRuntimeComponentsProvider LSMBTREE_PRIMARY_PROVIDER = new AsterixRuntimeComponentsProvider(
            LSMBTreeIOOperationCallbackFactory.INSTANCE, false);
    public static final AsterixRuntimeComponentsProvider LSMBTREE_SECONDARY_PROVIDER = new AsterixRuntimeComponentsProvider(
            LSMBTreeIOOperationCallbackFactory.INSTANCE, true);
    public static final AsterixRuntimeComponentsProvider LSMRTREE_PROVIDER = new AsterixRuntimeComponentsProvider(
            LSMRTreeIOOperationCallbackFactory.INSTANCE, true);
    public static final AsterixRuntimeComponentsProvider LSMINVERTEDINDEX_PROVIDER = new AsterixRuntimeComponentsProvider(
            LSMInvertedIndexIOOperationCallbackFactory.INSTANCE, true);
    public static final AsterixRuntimeComponentsProvider NOINDEX_PROVIDER = new AsterixRuntimeComponentsProvider(null,
            false);

    private AsterixRuntimeComponentsProvider(ILSMIOOperationCallbackFactory ioOpCallbackFactory, boolean isSecondary) {
        this.ioOpCallbackFactory = ioOpCallbackFactory;
        this.isSecondary = isSecondary;
    }

    @Override
    public ILSMOperationTracker getOperationTracker(IHyracksTaskContext ctx) {
        assert isSecondary;
        return new BaseOperationTracker(ioOpCallbackFactory);
    }

    @Override
    public ILSMIOOperationCallback getIOOperationCallback(ILSMIndex index) {
        return ((BaseOperationTracker) index.getOperationTracker()).getIOOperationCallback();
    }

    @Override
    public ILSMIOOperationScheduler getIOScheduler(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getLSMIOScheduler();
    }

    @Override
    public ILSMMergePolicy getMergePolicy(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getLSMMergePolicy();
    }

    @Override
    public IBufferCache getBufferCache(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getBufferCache();
    }

    @Override
    public IFileMapProvider getFileMapProvider(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getFileMapManager();
    }

    @Override
    public ILocalResourceRepository getLocalResourceRepository(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getLocalResourceRepository();
    }

    @Override
    public IIndexLifecycleManager getLifecycleManager(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getIndexLifecycleManager();
    }

    @Override
    public ResourceIdFactory getResourceIdFactory(IHyracksTaskContext ctx) {
        return ((IAsterixAppRuntimeContext) ctx.getJobletContext().getApplicationContext().getApplicationObject())
                .getResourceIdFactory();
    }

}