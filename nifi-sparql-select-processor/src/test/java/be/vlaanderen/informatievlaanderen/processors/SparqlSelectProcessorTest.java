/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.vlaanderen.informatievlaanderen.processors;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SparqlSelectProcessorTest {

    public static final ValueFactory vf = SimpleValueFactory.getInstance();

    private TestRunner testRunner;

    @BeforeEach
    public void init() {
        testRunner = TestRunners.newTestRunner(SparqlSelectProcessor.class);
    }

    public final String SelectQuery = """
        SELECT ?predicate ?object
        WHERE { ?subject ?predicate ?object }
        """;

    public final String FlowFileContents = """
        <http://data-in-flowfile/> <http://test/> "OBJ1" .
        <http://data-in-flowfile/> <http://test/2> "OBJ2" .
        <http://data-in-flowfile/> <http://test/2> "OBJ3" .
        """;

    /**
     * Assert that a SPARQL Construct processor can manipulate a FlowFile.
     */
    @Test
    public void testSparqlConstructInferenceMode() {
        final TestRunner testRunner = TestRunners.newTestRunner(new SparqlSelectProcessor());

        testRunner.setProperty(SparqlSelectProcessor.SPARQL_QUERY, SelectQuery);

        testRunner.enqueue(FlowFileContents);

        testRunner.run();

        MockFlowFile f = testRunner.getFlowFilesForRelationship(SparqlSelectProcessor.SUCCESS).get(0);
        assert f.getContent().equals("[{\"predicate\":\"http://test/\",\"object\":\"OBJ1\"},{\"predicate\":\"http://test/2\",\"object\":\"OBJ2\"},{\"predicate\":\"http://test/2\",\"object\":\"OBJ3\"}]");

    }

}
