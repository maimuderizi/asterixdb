package edu.uci.ics.asterix.common.context;

import java.io.IOException;
import java.util.logging.Level;

import edu.uci.ics.asterix.common.config.GlobalConfig;
import edu.uci.ics.asterix.transaction.management.exception.ACIDException;
import edu.uci.ics.asterix.transaction.management.service.transaction.TransactionProvider;
import edu.uci.ics.hyracks.api.application.INCApplicationContext;
import edu.uci.ics.hyracks.api.io.IIOManager;
import edu.uci.ics.hyracks.storage.am.common.api.IIndexLifecycleManager;
import edu.uci.ics.hyracks.storage.am.common.dataflow.IndexLifecycleManager;
import edu.uci.ics.hyracks.storage.common.TransientIndexArtifactMap;
import edu.uci.ics.hyracks.storage.common.buffercache.BufferCache;
import edu.uci.ics.hyracks.storage.common.buffercache.ClockPageReplacementStrategy;
import edu.uci.ics.hyracks.storage.common.buffercache.HeapBufferAllocator;
import edu.uci.ics.hyracks.storage.common.buffercache.IBufferCache;
import edu.uci.ics.hyracks.storage.common.buffercache.ICacheMemoryAllocator;
import edu.uci.ics.hyracks.storage.common.buffercache.IPageReplacementStrategy;
import edu.uci.ics.hyracks.storage.common.file.IFileMapManager;
import edu.uci.ics.hyracks.storage.common.file.IFileMapProvider;
import edu.uci.ics.hyracks.storage.common.file.IIndexArtifactMap;

public class AsterixAppRuntimeContext {
    private static final int DEFAULT_BUFFER_CACHE_PAGE_SIZE = 32768;
    private final INCApplicationContext ncApplicationContext;

    private IIndexArtifactMap indexArtifactMap;
    private IIndexLifecycleManager indexLifecycleManager;
    private IFileMapManager fileMapManager;
    private IBufferCache bufferCache;
    private TransactionProvider provider;

    public AsterixAppRuntimeContext(INCApplicationContext ncApplicationContext) {
        this.ncApplicationContext = ncApplicationContext;
    }

    public void initialize() throws IOException, ACIDException {
        int pageSize = getBufferCachePageSize();
        int numPages = getBufferCacheNumPages();

        indexArtifactMap = new TransientIndexArtifactMap();
        fileMapManager = new AsterixFileMapManager();
        ICacheMemoryAllocator allocator = new HeapBufferAllocator();
        IPageReplacementStrategy prs = new ClockPageReplacementStrategy();
        IIOManager ioMgr = ncApplicationContext.getRootContext().getIOManager();
        bufferCache = new BufferCache(ioMgr, allocator, prs, fileMapManager, pageSize, numPages, Integer.MAX_VALUE);
        indexLifecycleManager = new IndexLifecycleManager();
        provider = new TransactionProvider(ncApplicationContext.getNodeId());
    }

    private int getBufferCachePageSize() {
        int pageSize = DEFAULT_BUFFER_CACHE_PAGE_SIZE;
        String pageSizeStr = System.getProperty(GlobalConfig.BUFFER_CACHE_PAGE_SIZE_PROPERTY, null);
        if (pageSizeStr != null) {
            try {
                pageSize = Integer.parseInt(pageSizeStr);
            } catch (NumberFormatException nfe) {
                if (GlobalConfig.ASTERIX_LOGGER.isLoggable(Level.WARNING)) {
                    GlobalConfig.ASTERIX_LOGGER.warning("Wrong buffer cache page size argument. "
                            + "Using default value: " + pageSize);
                }
            }
        }

        if (GlobalConfig.ASTERIX_LOGGER.isLoggable(Level.INFO)) {
            GlobalConfig.ASTERIX_LOGGER.info("Buffer cache page size: " + pageSize);
        }

        return pageSize;
    }

    private int getBufferCacheNumPages() {
        int numPages = GlobalConfig.DEFAULT_BUFFER_CACHE_NUM_PAGES;
        String numPagesStr = System.getProperty(GlobalConfig.BUFFER_CACHE_NUM_PAGES_PROPERTY, null);
        if (numPagesStr != null) {
            try {
                numPages = Integer.parseInt(numPagesStr);
            } catch (NumberFormatException nfe) {
                if (GlobalConfig.ASTERIX_LOGGER.isLoggable(Level.WARNING)) {
                    GlobalConfig.ASTERIX_LOGGER.warning("Wrong buffer cache size argument. " + "Using default value: "
                            + numPages);
                }
                return numPages;
            }
        }

        if (GlobalConfig.ASTERIX_LOGGER.isLoggable(Level.INFO)) {
            GlobalConfig.ASTERIX_LOGGER.info("Buffer cache size (number of pages): " + numPages);
        }

        return numPages;
    }

    public void deinitialize() {
        bufferCache.close();
    }

    public IBufferCache getBufferCache() {
        return bufferCache;
    }

    public IFileMapProvider getFileMapManager() {
        return fileMapManager;
    }

    public TransactionProvider getTransactionProvider() {
        return provider;
    }

    public IIndexLifecycleManager getIndexLifecycleManager() {
        return indexLifecycleManager;
    }

    public IIndexArtifactMap getIndexArtifactMap() {
        return indexArtifactMap;
    }

}