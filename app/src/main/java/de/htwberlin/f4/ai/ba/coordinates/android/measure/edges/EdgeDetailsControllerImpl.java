package de.htwberlin.f4.ai.ba.coordinates.android.measure.edges;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.ContactsContract;

import com.example.carol.bvg.R;

import java.util.List;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeImplementation;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by benni on 13.08.2017.
 */

public class EdgeDetailsControllerImpl implements EdgeDetailsController {

    private EdgeDetailsView view;

    private Node startNode;
    private Node targetNode;
    private Edge edge;

    @Override
    public void setView(EdgeDetailsView view) {
        this.view = view;
    }

    @Override
    public void setNodes(String startNodeId, String targetNodeId) {
        DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        startNode = databaseHandler.getNode(startNodeId);
        targetNode = databaseHandler.getNode(targetNodeId);
    }

    @Override
    public void onDeleteClicked() {
        if (edge != null) {
            // ask the user if the way was handycap friendly and save measurement data afterwards
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Weg löschen");
            alertDialogBuilder.setMessage("Sind Sie sicher, dass Sie den Weg löschen möchten?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.trash);

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteEdge();
                    dialog.dismiss();
                    view.finish();
                }
            });

            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();



        }
    }

    private void deleteEdge() {
        if (edge != null) {
            DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(view.getContext());
            databaseHandler.deleteEdge(edge);
        }
    }

    @Override
    public void onSaveClicked() {
        // since there is no update method yet, delete old edge and create new
        if (edge != null) {
            Edge updatedEdge = new EdgeImplementation(edge.getNodeA(), edge.getNodeB(), edge.getAccessibly(), edge.getStepCoordsList(), edge.getWeight(), edge.getAdditionalInfo());
            DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(view.getContext());
            databaseHandler.deleteEdge(edge);
            databaseHandler.insertEdge(updatedEdge);
            // save our updated edge object
            edge = updatedEdge;
            view.finish();
        }
    }

    @Override
    public void onHandycapChanged(boolean handycapFriendly) {
        if (edge != null) {
            edge.setAccessibly(handycapFriendly);
        }
    }

    @Override
    public void onEdgeInfoChanged(String info) {
        if (edge != null) {
            edge.setAdditionalInfo(info);
        }
    }

    @Override
    public void onEdgeDistanceChanged(float distance) {
        if (edge != null) {
            // since the distance is calculated in meters we need to convert it into cm for edge weight
            int weightCm = Math.round(distance*100);
            edge.setWeight(weightCm);
        }
    }

    @Override
    public void onResume() {
        if (startNode != null && targetNode != null) {
            view.updateStartNodeInfo(startNode);
            view.updateTargetNodeInfo(targetNode);

            Edge existingEdge = findEdge(startNode, targetNode);
            // save edge for later usage
            edge = existingEdge;
            view.updateEdgeInfo(edge);
        }
    }

    private Edge findEdge(Node start, Node target) {
        DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        List<Edge> edgeList = databaseHandler.getAllEdges();
        Edge existingEdge = null;

        for (Edge edge : edgeList) {
            if ( (edge.getNodeA().getId().equals(start.getId()) && edge.getNodeB().getId().equals(target.getId())) ||
                    (edge.getNodeA().getId().equals(target.getId()) && edge.getNodeB().getId().equals(start.getId())) ) {
                existingEdge = edge;
                break;
            }
        }

        return existingEdge;
    }
}
