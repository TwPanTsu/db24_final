package org.vanilladb.bench.server.param.sift;

import java.util.Set;
import java.util.logging.Logger;

import org.vanilladb.core.query.algebra.vector.NearestNeighborScan;
import org.vanilladb.core.sql.IntegerConstant;
import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.Type;
import org.vanilladb.core.sql.VectorConstant;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureHelper;

public class SiftBenchParamHelper implements StoredProcedureHelper {
    private static Logger logger = Logger.getLogger(SiftBenchParamHelper.class.getName());
    private final String table = "sift";
    private final String embField = "i_emb";
    private VectorConstant query;
    private int numDimension;
    private Integer[] items;
    private int numNeighbors = 20; // Number of top-k

    @Override
    public void prepareParameters(Object... pars) {
        numDimension = (Integer) pars[0];
        float[] rawVector = new float[numDimension];
        for (int i = 0; i < numDimension; i++) {
            rawVector[i] = (float) pars[i+1];
        }
        query = new VectorConstant(rawVector);
        items = new Integer[numNeighbors];
    }

    @Override
    public Schema getResultSetSchema() {
        Schema sch = new Schema();
        sch.addField("rc", Type.INTEGER);
        for (int i = 0; i < numNeighbors; i++) {
            sch.addField("id_" + i, Type.INTEGER);
        }
        return sch;
    }

    @Override
    public SpResultRecord newResultSetRecord() {
        SpResultRecord rec = new SpResultRecord();
        rec.setVal("rc", new IntegerConstant(numNeighbors));
        for (int i = 0; i < numNeighbors; i++) {
            if (items[i]==null)
                logger.info(i+":items:null");
            rec.setVal("id_" + i, new IntegerConstant(items[i]));
        }
        return rec;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public void setNearestNeighbors(Set<Integer> nearestNeighbors) {
        // logger.info("size:"+nearestNeighbors.size());
        items = nearestNeighbors.toArray(items);
    }

    public String getTableName() {
        return table;
    }

    public String getEmbeddingField() {
        return embField;
    }

    public VectorConstant getQuery() {
        return query;
    }

    public int getK() {
        return numNeighbors;
    }
}
