package org.vanilladb.bench.server.procedure.sift;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.vanilladb.bench.server.param.sift.SiftBenchParamHelper;
import org.vanilladb.bench.server.procedure.StoredProcedureUtils;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.VectorConstant;
import org.vanilladb.core.sql.storedprocedure.StoredProcedure;
import org.vanilladb.core.storage.tx.Transaction;

public class SiftBenchProc extends StoredProcedure<SiftBenchParamHelper> {
    private static Logger logger = Logger.getLogger(SiftBenchProc.class.getName());

    public SiftBenchProc() {
        super(new SiftBenchParamHelper());
    }

    @Override
    protected void executeSql() {
        SiftBenchParamHelper paramHelper = getHelper();
        VectorConstant query = paramHelper.getQuery();
        Transaction tx = getTransaction();

        String nnQuery = "SELECT i_id FROM " + paramHelper.getTableName() + 
            " ORDER BY " + paramHelper.getEmbeddingField() + " <EUC> " + query.toString() + " LIMIT " + paramHelper.getK();

        // Execute nearest neighbor search
        Scan nearestNeighborScan = StoredProcedureUtils.executeQuery(nnQuery, tx);
        
        nearestNeighborScan.beforeFirst();
        
        Set<Integer> nearestNeighbors = new HashSet<>();

        int count = 0;
        while (nearestNeighborScan.next()) {
            Integer a = (Integer) nearestNeighborScan.getVal("i_id").asJavaVal();
            nearestNeighbors.add(a);
            count++;
        }
        // logger.info("SiftBenchProc size:"+nearestNeighbors.size());
        // logger.info("SiftBenchProc count:"+count);

        nearestNeighborScan.close();

        if (count == 0)
            throw new RuntimeException("Nearest neighbor query execution failed for " + query.toString());
        
        paramHelper.setNearestNeighbors(nearestNeighbors);
    }
    
}
