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
package edu.uci.ics.asterix.runtime.evaluators.functions.temporal;

import java.io.DataOutput;

import edu.uci.ics.asterix.dataflow.data.nontagged.serde.ADateTimeSerializerDeserializer;
import edu.uci.ics.asterix.formats.nontagged.AqlSerializerDeserializerProvider;
import edu.uci.ics.asterix.om.base.ADuration;
import edu.uci.ics.asterix.om.base.AMutableDuration;
import edu.uci.ics.asterix.om.base.ANull;
import edu.uci.ics.asterix.om.functions.AsterixBuiltinFunctions;
import edu.uci.ics.asterix.om.functions.IFunctionDescriptor;
import edu.uci.ics.asterix.om.functions.IFunctionDescriptorFactory;
import edu.uci.ics.asterix.om.types.ATypeTag;
import edu.uci.ics.asterix.om.types.BuiltinType;
import edu.uci.ics.asterix.om.types.EnumDeserializer;
import edu.uci.ics.asterix.runtime.evaluators.base.AbstractScalarFunctionDynamicDescriptor;
import edu.uci.ics.hyracks.algebricks.common.exceptions.AlgebricksException;
import edu.uci.ics.hyracks.algebricks.core.algebra.functions.FunctionIdentifier;
import edu.uci.ics.hyracks.algebricks.runtime.base.ICopyEvaluator;
import edu.uci.ics.hyracks.algebricks.runtime.base.ICopyEvaluatorFactory;
import edu.uci.ics.hyracks.api.dataflow.value.ISerializerDeserializer;
import edu.uci.ics.hyracks.api.exceptions.HyracksDataException;
import edu.uci.ics.hyracks.data.std.api.IDataOutputProvider;
import edu.uci.ics.hyracks.data.std.util.ArrayBackedValueStorage;
import edu.uci.ics.hyracks.dataflow.common.data.accessors.IFrameTupleReference;

public class SubtractDatetimeDescriptor extends AbstractScalarFunctionDynamicDescriptor {

    private final static long serialVersionUID = 1L;
    public final static FunctionIdentifier FID = AsterixBuiltinFunctions.SUBTRACT_DATETIME;

    // allowed input types
    private final static byte SER_NULL_TYPE_TAG = ATypeTag.NULL.serialize();
    private final static byte SER_DATETIME_TYPE_TAG = ATypeTag.DATETIME.serialize();

    public final static IFunctionDescriptorFactory FACTORY = new IFunctionDescriptorFactory() {

        @Override
        public IFunctionDescriptor createFunctionDescriptor() {
            return new SubtractDatetimeDescriptor();
        }
    };

    /* (non-Javadoc)
     * @see edu.uci.ics.asterix.runtime.base.IScalarFunctionDynamicDescriptor#createEvaluatorFactory(edu.uci.ics.hyracks.algebricks.runtime.base.ICopyEvaluatorFactory[])
     */
    @Override
    public ICopyEvaluatorFactory createEvaluatorFactory(final ICopyEvaluatorFactory[] args) throws AlgebricksException {
        return new ICopyEvaluatorFactory() {

            private static final long serialVersionUID = 1L;

            @Override
            public ICopyEvaluator createEvaluator(final IDataOutputProvider output) throws AlgebricksException {
                return new ICopyEvaluator() {

                    private DataOutput out = output.getDataOutput();
                    private ArrayBackedValueStorage argOut0 = new ArrayBackedValueStorage();
                    private ArrayBackedValueStorage argOut1 = new ArrayBackedValueStorage();
                    private ICopyEvaluator eval0 = args[0].createEvaluator(argOut0);
                    private ICopyEvaluator eval1 = args[1].createEvaluator(argOut1);

                    // possible output types
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ANull> nullSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.ANULL);
                    @SuppressWarnings("unchecked")
                    private ISerializerDeserializer<ADuration> durationSerde = AqlSerializerDeserializerProvider.INSTANCE
                            .getSerializerDeserializer(BuiltinType.ADURATION);

                    private AMutableDuration aDuration = new AMutableDuration(0, 0);

                    @Override
                    public void evaluate(IFrameTupleReference tuple) throws AlgebricksException {
                        argOut0.reset();
                        eval0.evaluate(tuple);
                        argOut1.reset();
                        eval1.evaluate(tuple);

                        try {
                            if (argOut0.getByteArray()[0] == SER_NULL_TYPE_TAG
                                    || argOut1.getByteArray()[0] == SER_NULL_TYPE_TAG) {
                                nullSerde.serialize(ANull.NULL, out);
                                return;
                            }

                            if (argOut0.getByteArray()[0] != SER_DATETIME_TYPE_TAG
                                    && argOut1.getByteArray()[0] != SER_DATETIME_TYPE_TAG) {
                                throw new AlgebricksException(FID.getName()
                                        + ": expects input type (DATETIME/NULL, DATETIME/NULL) but got ("
                                        + EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(argOut0.getByteArray()[0])
                                        + ", "
                                        + EnumDeserializer.ATYPETAGDESERIALIZER.deserialize(argOut1.getByteArray()[0])
                                        + ").");
                            }

                            long durationChronon = ADateTimeSerializerDeserializer
                                    .getChronon(argOut0.getByteArray(), 1)
                                    - ADateTimeSerializerDeserializer.getChronon(argOut1.getByteArray(), 1);

                            aDuration.setValue(0, durationChronon);

                            durationSerde.serialize(aDuration, out);

                        } catch (HyracksDataException hex) {
                            throw new AlgebricksException(hex);
                        }
                    }
                };
            }
        };
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.asterix.om.functions.IFunctionDescriptor#getIdentifier()
     */
    @Override
    public FunctionIdentifier getIdentifier() {
        return FID;
    }

}
