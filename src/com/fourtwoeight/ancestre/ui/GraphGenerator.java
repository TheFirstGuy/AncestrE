package com.fourtwoeight.ancestre.ui;

import com.fourtwoeight.ancestre.model.Person;
import org.graphsfx.graph.CircularReferenceException;
import org.graphsfx.graph.Graph;
import org.graphsfx.graph.TreeGraph;
import org.graphsfx.model.GraphNode;

import java.util.HashMap;

public class GraphGenerator {

    /**
     * Generates a TreeGraph that represents the ancestry of the passed in Person. It also provides a HashMap of the
     * Person to GraphNode mappings created
     * @param person The Person object to get the ancestry for
     * @param graph The generated graph
     * @param nodes The generated mapping of Person objects to GraphNodes
     * @return 'true' if tree generated successfully, 'false' otherwise
     */
    public static boolean generateAncestry(Person person,
                                           TreeGraph graph,
                                           HashMap<Person, GraphNode> nodes) throws CircularReferenceException {
        boolean generated = false;
        nodes.clear();

        GraphNode root = generateAncestryHelper(person, nodes);
        graph.addGraphNode(root);
        graph.setRootNode(root);

        return generated;
    }

    // Private Methods =================================================================================================

    /**
     * Helper Method for generateAncestry. Recursively generates tree and adds Person to GraphNode mappings.
     * @param person The person to generate a tree from
     * @param nodes Person to GraphNode mappings
     * @return The generated graphNode for the passed in person
     */
    private static GraphNode generateAncestryHelper(Person person, HashMap<Person, GraphNode> nodes){
        GraphNode graphNode = null;

        if(!nodes.containsKey(person)){
            graphNode = new GraphNode(person.getFullName());
            nodes.put(person, graphNode);

            // Set adjacencies
            Person mother = person.getMother();
            Person father = person.getFather();

            if(mother != null){
                graphNode.addAdjacency(generateAncestryHelper(mother, nodes));
            }
            if(father != null){
                graphNode.addAdjacency(generateAncestryHelper(father, nodes));
            }
        }

        return graphNode;
    }
}
