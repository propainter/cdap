package com.continuuity.data.operation.ttqueue;

import com.continuuity.api.data.OperationException;
import com.continuuity.common.conf.CConfiguration;
import com.continuuity.data.runtime.DataFabricLocalModule;
import com.continuuity.data.table.OVCTableHandle;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;

import java.util.Random;

// TODO: fix HyperSQL functions and enable test
@Ignore
public class TestHyperSQLNewTTQueue extends TestTTQueueNew {

  private static final Injector injector = Guice.createInjector (
    new DataFabricLocalModule("jdbc:hsqldb:mem:membenchdb", null));
  //  Guice.createInjector(new DataFabricLocalModule());

  private static final OVCTableHandle handle =
    injector.getInstance(OVCTableHandle.class);

  private static final Random r = new Random();

  @Override
  protected TTQueue createQueue(CConfiguration conf) throws OperationException {
    String rand = "" + Math.abs(r.nextInt());
    updateCConfiguration(conf);
    return new TTQueueNewOnVCTable(
        handle.getTable(Bytes.toBytes("HyperSQLTTQueueFifoOnVCTable" + rand)),
        Bytes.toBytes("TestTTQueueName" + rand),
        TestTTQueue.oracle, conf);
  }

  @Override
  protected int getNumIterations() {
    return 201;
  }
}
